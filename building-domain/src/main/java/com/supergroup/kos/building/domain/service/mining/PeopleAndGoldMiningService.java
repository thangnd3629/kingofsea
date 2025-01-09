package com.supergroup.kos.building.domain.service.mining;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

import org.springframework.stereotype.Service;

import com.supergroup.kos.building.domain.model.mining.PeopleAndGoldMiningResult;
import com.supergroup.kos.building.domain.model.mining.PeopleAndGoldMiningSnapshot;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PeopleAndGoldMiningService extends MiningService<PeopleAndGoldMiningSnapshot, PeopleAndGoldMiningResult> {
    @Override
    PeopleAndGoldMiningResult calculateMiningClaim(PeopleAndGoldMiningSnapshot latestSnapshot, LocalDateTime now) {

        var result = new PeopleAndGoldMiningResult();

        if (Objects.nonNull(latestSnapshot.getLastTimeClaim())) {
            var diffTime = Duration.between(latestSnapshot.getLastTimeClaim(), now).getSeconds();
            if(diffTime < 1) {
                result.setLastTimeClaim(latestSnapshot.getLastTimeClaim());
                return result.setIncreasePeople(0D).setIncreaseGold(0D);
            }
            var threshold = latestSnapshot.getMpMultiplier() * latestSnapshot.getMp();
            var populationThresholdRound = (long) Math.ceil(threshold) + 1L;
            var speedWhenUnderThreshold =
                    threshold * latestSnapshot.getPopulationGrowthBase() / latestSnapshot.getMaxPopulation(); //

            Double idlePeople = 0D;
            Double goldIncrease = ((long) Math.floor(latestSnapshot.getIdlePeople())) * latestSnapshot.getGoldPerPerson() * diffTime;
            Double timeRemain = 0D;
            if (latestSnapshot.getIdlePeople() - Math.floor(latestSnapshot.getIdlePeople()) == 0) {
                timeRemain = Double.valueOf(diffTime);
                idlePeople = latestSnapshot.getIdlePeople();
            } else {
                if (latestSnapshot.getIdlePeople() + latestSnapshot.getPeopleInWork() < populationThresholdRound) {
                    Double timeNeed = (1 - (latestSnapshot.getIdlePeople() - Math.floor(latestSnapshot.getIdlePeople()))) / speedWhenUnderThreshold;
                    timeRemain = diffTime - timeNeed;
                    if(timeRemain <= 0) {
                        Double peopleIncrease = speedWhenUnderThreshold * diffTime;
                        result.setLastTimeClaim(now);
                        return result.setIncreasePeople(peopleIncrease).setIncreaseGold(goldIncrease);
                    }
                    goldIncrease += 1 * timeRemain * latestSnapshot.getGoldPerPerson();
                    idlePeople = Math.ceil(latestSnapshot.getIdlePeople());
                } else {
                    Double speedWhenOverThreshold =
                            latestSnapshot.getPopulationGrowthBase() / (latestSnapshot.getIdlePeople() + latestSnapshot.getPeopleInWork()
                                                                        - threshold);
                    Double timeNeed = (1 - (latestSnapshot.getIdlePeople() - Math.floor(latestSnapshot.getIdlePeople()))) / speedWhenOverThreshold;
                    timeRemain = diffTime - timeNeed;
                    if(timeRemain <= 0) {
                        Double peopleIncrease = speedWhenOverThreshold * diffTime;
                        result.setLastTimeClaim(now);
                        return result.setIncreasePeople(peopleIncrease).setIncreaseGold(goldIncrease);
                    }
                    goldIncrease += 1 * timeRemain * latestSnapshot.getGoldPerPerson();
                    idlePeople = Math.ceil(latestSnapshot.getIdlePeople());
                }
            }

            Double peopleIncrease = 0D;
            Long peopleWork = latestSnapshot.getPeopleInWork();
            Double totalPeople = peopleWork + idlePeople;
            if (totalPeople < populationThresholdRound) {
                peopleIncrease = timeRemain * speedWhenUnderThreshold;
                if (totalPeople + peopleIncrease <= populationThresholdRound) {
                    //mining gold
                    if (peopleIncrease >= 1) {
                        goldIncrease += calculateGoldIncreaseWhenPeopleIncreaseUnderThreshold(peopleIncrease, latestSnapshot.getGoldPerPerson(),
                                                                                              speedWhenUnderThreshold);
                    }
                    //mining gold
                } else {
                    Double timeToReachThreshold = (populationThresholdRound - totalPeople) / speedWhenUnderThreshold;
                    goldIncrease += calculateGoldIncreaseWhenPeopleIncreaseUnderThreshold(populationThresholdRound - totalPeople,
                                                                                          latestSnapshot.getGoldPerPerson(), speedWhenUnderThreshold);
                    Double timeRemaining = timeRemain - timeToReachThreshold;
                    goldIncrease += timeRemaining * (populationThresholdRound - totalPeople) * latestSnapshot.getGoldPerPerson();

                    Double peopleIncreaseWhenCrossThreshold = calculatePeopleIncreaseWhenCrossThreshold(populationThresholdRound - threshold,
                                                                                                        timeRemaining,
                                                                                                        latestSnapshot.getPopulationGrowthBase());
                    if (populationThresholdRound + peopleIncreaseWhenCrossThreshold > latestSnapshot.getMaxPopulation()) {
                        peopleIncrease = latestSnapshot.getMaxPopulation() - totalPeople;
                    } else {
                        peopleIncrease = populationThresholdRound - totalPeople + peopleIncreaseWhenCrossThreshold;
                    }

                    if (peopleIncreaseWhenCrossThreshold > 1) {
                        if (populationThresholdRound + peopleIncreaseWhenCrossThreshold > latestSnapshot.getMaxPopulation()) {
                            Double people = (double) (latestSnapshot.getMaxPopulation() - populationThresholdRound);
                            goldIncrease += calculateGold(people, latestSnapshot.getPopulationGrowthBase(),
                                                          2 * (populationThresholdRound - threshold) - 1,
                                                          latestSnapshot.getGoldPerPerson());
                            Double timeNeedToMax = (people * people + (2 * (populationThresholdRound - threshold) - 1) * people) / (2
                                                                                                                                    * latestSnapshot.getPopulationGrowthBase());
                            goldIncrease += people * (timeRemaining - timeNeedToMax) * latestSnapshot.getGoldPerPerson();

                        } else {
                            Double peopleRound = Math.floor(peopleIncreaseWhenCrossThreshold);
                            goldIncrease += calculateGold(peopleRound, latestSnapshot.getPopulationGrowthBase(),
                                                          2 * (populationThresholdRound - threshold) - 1, latestSnapshot.getGoldPerPerson());
                            Double timeToNeed = (peopleRound * peopleRound + (2 * (populationThresholdRound - threshold) - 1) * peopleRound) / (2
                                                                                                                                                * latestSnapshot.getPopulationGrowthBase());
                            goldIncrease += peopleRound * (timeRemaining - timeToNeed) * latestSnapshot.getGoldPerPerson();
                        }
                    }
                }

            } else {
                Double peopleIncreaseWhenCrossThreshold = calculatePeopleIncreaseWhenCrossThreshold(totalPeople - threshold,
                                                                                                    timeRemain,
                                                                                                    latestSnapshot.getPopulationGrowthBase());
                if (totalPeople + peopleIncreaseWhenCrossThreshold > latestSnapshot.getMaxPopulation()) {
                    peopleIncrease = latestSnapshot.getMaxPopulation() - totalPeople;
                    if (peopleIncrease > 1) {
                        goldIncrease += calculateGold(peopleIncrease, latestSnapshot.getPopulationGrowthBase(),
                                                      2 * (totalPeople - threshold) - 1, latestSnapshot.getGoldPerPerson());
                        Double timeNeedToMax = (peopleIncrease * peopleIncrease + (2 * (totalPeople - threshold) - 1) * peopleIncrease) / (2
                                                                                                                                               * latestSnapshot.getPopulationGrowthBase());
                        goldIncrease += peopleIncrease * (timeRemain - timeNeedToMax) * latestSnapshot.getGoldPerPerson();
                    }

                } else {
                    peopleIncrease = peopleIncreaseWhenCrossThreshold;
                    if (peopleIncreaseWhenCrossThreshold > 1) {
                        Double peopleRound = Math.floor(peopleIncreaseWhenCrossThreshold);
                        goldIncrease += calculateGold(peopleRound, latestSnapshot.getPopulationGrowthBase(),
                                                      2 * (totalPeople - threshold) - 1, latestSnapshot.getGoldPerPerson());
                        Double timeToNeed = (peopleRound * peopleRound + (2 * (totalPeople - threshold) - 1) * peopleRound) / (2
                                                                                                                               * latestSnapshot.getPopulationGrowthBase());
                        goldIncrease += peopleRound * (timeRemain - timeToNeed) * latestSnapshot.getGoldPerPerson();
                    }
                }
            }

//            if(idlePeople + latestSnapshot.getPeopleInWork() < populationThresholdRound) {
//                peopleIncrease = diffTime * speedWhenUnderThreshold;
//                if(idlePeople + peopleIncrease + latestSnapshot.getPeopleInWork() > populationThresholdRound)
//                var timeToReachThreshold = Math.round((populationThresholdRound - latestSnapshot.getPeopleInWork() - idlePeople) / speedWhenUnderThreshold);
//            }

//            Double peopleMiningGold = latestSnapshot.getIdlePeople() - (Math.floor(latestSnapshot.getIdlePeople()));
//            if (idlePeople + peopleWork  < populationThresholdRound) {
//                peopleIncrease = timeRemain * speedWhenUnderThreshold;
//                if (idlePeople+ peopleWork + peopleIncrease > populationThresholdRound) {
//                    var timeToReachThreshold = Math.round((populationThresholdRound - latestSnapshot.getIdlePeople() - peopleWork) / speedWhenUnderThreshold);
//                    double timeRemaining = timeRemain - timeToReachThreshold;
//                    Double peopleIncreaseWhenCrossThreshold = calculatePeopleIncreaseWhenCrossThreshold(populationThresholdRound - threshold,
//                                                                                                        timeRemaining,
//                                                                                                        latestSnapshot.getPopulationGrowthBase());
//                    if (populationThresholdRound + peopleIncreaseWhenCrossThreshold > latestSnapshot.getMaxPopulation()) {
//                        peopleIncrease = latestSnapshot.getMaxPopulation().doubleValue() - latestSnapshot.getIdlePeople();
//
//                        // mining gold
//                        if (peopleIncrease + peopleMiningGold > 1) {
//                            goldIncrease += calculateGoldIncreaseWhenPeopleIncreaseUnderThreshold(
//                                    populationThresholdRound - latestSnapshot.getIdlePeople(), latestSnapshot.getGoldPerPerson(),
//                                    speedWhenUnderThreshold);
//                            goldIncrease += calculateGold((double) (latestSnapshot.getMaxPopulation() - populationThresholdRound),
//                                                          latestSnapshot.getPopulationGrowthBase(), 2 * (populationThresholdRound - threshold) - 1,
//                                                          latestSnapshot.getGoldPerPerson());
//
//                            Double people = (double) (latestSnapshot.getMaxPopulation() - populationThresholdRound);
//                            Double timeNeedToMaxPopulation =
//                                    diffTime - (populationThresholdRound - latestSnapshot.getIdlePeople()) / speedWhenUnderThreshold
//                                    - (1 / (2 * latestSnapshot.getPopulationGrowthBase())) * ((people * people) + people * (
//                                            2 * (populationThresholdRound - threshold) - 1));
//                            goldIncrease +=
//                                    (latestSnapshot.getMaxPopulation() - latestSnapshot.getIdlePeople()) * (diffTime - timeNeedToMaxPopulation)
//                                    * latestSnapshot.getGoldPerPerson();
//                        }
//
//                        // mining gold
//                    } else {
//                        // mining gold
//                        if (peopleIncrease + peopleMiningGold > 1) {
//                            peopleIncrease = populationThresholdRound - latestSnapshot.getIdlePeople() + peopleIncreaseWhenCrossThreshold;
//                            goldIncrease += calculateGoldIncreaseWhenPeopleIncreaseUnderThreshold(
//                                    populationThresholdRound - latestSnapshot.getIdlePeople(), latestSnapshot.getGoldPerPerson(),
//                                    speedWhenUnderThreshold);
//                            Double timeNeedToThreshold = (populationThresholdRound - latestSnapshot.getIdlePeople()) / speedWhenUnderThreshold;
//                            goldIncrease += Math.ceil(populationThresholdRound - latestSnapshot.getIdlePeople()) * (diffTime - timeNeedToThreshold)
//                                            * latestSnapshot.getGoldPerPerson();
//
//                            goldIncrease += calculateGold(peopleIncrease - (populationThresholdRound - threshold),
//                                                          latestSnapshot.getPopulationGrowthBase(), 2 * (populationThresholdRound - threshold) - 1,
//                                                          latestSnapshot.getGoldPerPerson());
//                        }
//                        // mining gold
//                    }
//                }
//
//                // mining gold
//                if (peopleIncrease + peopleMiningGold > 1) {
//                    goldIncrease += calculateGoldIncreaseWhenPeopleIncreaseUnderThreshold(peopleIncrease + peopleMiningGold,
//                                                                                          latestSnapshot.getGoldPerPerson(), speedWhenUnderThreshold);
//                }
//
//                // mining gold
//
//            } else {
//                Double peopleIncreaseWhenCrossThreshold = calculatePeopleIncreaseWhenCrossThreshold(latestSnapshot.getIdlePeople() - threshold,
//                                                                                                    diffTime,
//                                                                                                    latestSnapshot.getPopulationGrowthBase());
//                if (latestSnapshot.getIdlePeople() + peopleIncreaseWhenCrossThreshold > latestSnapshot.getMaxPopulation()) {
//                    peopleIncrease = latestSnapshot.getMaxPopulation() - latestSnapshot.getIdlePeople();
//                    // mining gold
//                    if (peopleIncrease + peopleMiningGold > 1) {
//                        Double speedMiningPeople = (latestSnapshot.getPopulationGrowthBase()) / (latestSnapshot.getIdlePeople() - threshold);
//                        Double timeNeedCreateOnePeople = (1 - peopleMiningGold) / speedMiningPeople;
//                        Double timeRemain = diffTime - timeNeedCreateOnePeople;
//                        goldIncrease += latestSnapshot.getGoldPerPerson() * timeRemain;
//
//                        Double a = 2 * (latestSnapshot.getIdlePeople() - threshold) - 1;
//                        Double people = Math.floor(latestSnapshot.getIdlePeople());
//                        goldIncrease += calculateGold(people, latestSnapshot.getPopulationGrowthBase(), a,
//                                                      latestSnapshot.getGoldPerPerson());
//                        Double timeNeed = (people * people + a * people) / (2 * latestSnapshot.getPopulationGrowthBase());
//                        goldIncrease += people * (timeRemain - timeNeed) * latestSnapshot.getGoldPerPerson();
//                    }
//
//                    // mining gold
//                } else {
//                    peopleIncrease = peopleIncreaseWhenCrossThreshold;
//                    // mining gold
//                    if (peopleIncrease + peopleMiningGold > 1) {
//                        Double speedMiningPeople = (latestSnapshot.getPopulationGrowthBase()) / (latestSnapshot.getIdlePeople() - threshold);
//                        Double timeNeedCreateOnePeople = (1 - peopleMiningGold) / speedMiningPeople;
//                        Double timeRemain = diffTime - timeNeedCreateOnePeople;
//                        goldIncrease += latestSnapshot.getGoldPerPerson() * timeRemain;
//
//                        Double peopleMining = Math.floor(peopleIncrease - (1 - peopleMiningGold));
//                        Double a = 2 * (Math.ceil(latestSnapshot.getIdlePeople()) - threshold) - 1;
//                        goldIncrease += calculateGold(peopleMining, latestSnapshot.getPopulationGrowthBase(), a, latestSnapshot.getGoldPerPerson());
//                    }
//                    // td
//                    // mining gold
//                }
//            }
            result.setIncreasePeople(peopleIncrease + idlePeople - latestSnapshot.getIdlePeople()).setIncreaseGold(goldIncrease);
        } else {
            result.setIncreaseGold(0.0).setIncreasePeople(0.0);
        }
        result.setLastTimeClaim(now);
        return result;
    }

    /**
     * The time it takes for n people to be created : (n * n + (2diffPeople -1) * n) / (2 * speedBase)
     **/
    public Double calculatePeopleIncreaseWhenCrossThreshold(Double diffPeople, double time, Double speedBase) {
        var delta = ((2 * diffPeople - 1) * (2 * diffPeople - 1) + 8 * speedBase * time);
        Double x = (-(2 * diffPeople - 1) + Math.sqrt(delta)) / 2;
        return x;
    }

    public Double calculateGold(Double peopleIncrease, Double populationGrowthBase, Double a, Double goldPerPerson) {
        Double n = peopleIncrease;
        double goldIncrease = (goldPerPerson / (2 * populationGrowthBase)) * ((a * (n - 1) * n / 2) - n * (n - 1) / 2 - (n - 2) * (n - 1) * n / 3
                                                                              + (n - 1) * n * n);
        return goldIncrease > 0 ? goldIncrease : 0;

    }

    public Double calculateGoldIncreaseWhenPeopleIncreaseUnderThreshold(Double people, Double goldPerPerson, Double speedWhenUnderThreshold) {
        Double peopleRound = Math.floor(people);
        Double timeNeedMiningOnePeople = 1 / speedWhenUnderThreshold;
        return goldPerPerson * timeNeedMiningOnePeople * (peopleRound * (peopleRound - 1) / 2)
               + goldPerPerson * peopleRound * (people - peopleRound) * timeNeedMiningOnePeople;
    }
}

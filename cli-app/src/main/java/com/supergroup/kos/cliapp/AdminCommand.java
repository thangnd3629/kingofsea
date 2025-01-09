package com.supergroup.kos.cliapp;

import java.sql.Time;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.supergroup.admin.domain.command.CreateAdminAccountCommand;
import com.supergroup.admin.domain.service.AdminAccountService;
import com.supergroup.auth.domain.repository.persistence.UserRepository;
import com.supergroup.core.constant.ErrorCode;
import com.supergroup.core.exception.KOSException;
import com.supergroup.kos.building.domain.command.GetMpFromQueenCommand;
import com.supergroup.kos.building.domain.command.GetMpFromRelicCommand;
import com.supergroup.kos.building.domain.command.SaveOrUpdateElementCommand;
import com.supergroup.kos.building.domain.command.UserCommand;
import com.supergroup.kos.building.domain.constant.EscortShipType;
import com.supergroup.kos.building.domain.constant.battle.BattleStatus;
import com.supergroup.kos.building.domain.constant.battle.BattleType;
import com.supergroup.kos.building.domain.constant.battle.FactionType;
import com.supergroup.kos.building.domain.constant.item.ItemId;
import com.supergroup.kos.building.domain.constant.seamap.SeaActivityStatus;
import com.supergroup.kos.building.domain.mapper.SeaElementMapper;
import com.supergroup.kos.building.domain.model.battle.Battle;
import com.supergroup.kos.building.domain.model.battle.BattleProfile;
import com.supergroup.kos.building.domain.model.battle.BattleReport;
import com.supergroup.kos.building.domain.model.battle.BattleRound;
import com.supergroup.kos.building.domain.model.battle.DamageReport;
import com.supergroup.kos.building.domain.model.battle.EscortShipReport;
import com.supergroup.kos.building.domain.model.battle.MotherShipReport;
import com.supergroup.kos.building.domain.model.battle.ReverseEscortShipReport;
import com.supergroup.kos.building.domain.model.battle.ReverseMotherShipReport;
import com.supergroup.kos.building.domain.model.battle.RoundReport;
import com.supergroup.kos.building.domain.model.battle.RoundUsedItem;
import com.supergroup.kos.building.domain.model.config.EscortShipConfig;
import com.supergroup.kos.building.domain.model.config.EscortShipGroupLevelConfig;
import com.supergroup.kos.building.domain.model.config.seamap.SeaElementConfig;
import com.supergroup.kos.building.domain.model.item.Item;
import com.supergroup.kos.building.domain.model.profile.KosProfile;
import com.supergroup.kos.building.domain.model.seamap.BossSea;
import com.supergroup.kos.building.domain.model.seamap.Coordinates;
import com.supergroup.kos.building.domain.model.seamap.RefreshNpcAndMineResult;
import com.supergroup.kos.building.domain.model.seamap.SeaActivity;
import com.supergroup.kos.building.domain.model.seamap.SeaElement;
import com.supergroup.kos.building.domain.model.seamap.SeaMapRefreshTransaction;
import com.supergroup.kos.building.domain.model.seamap.ShipElement;
import com.supergroup.kos.building.domain.model.seamap.movesession.UserBaseMoveSession;
import com.supergroup.kos.building.domain.model.ship.EscortShip;
import com.supergroup.kos.building.domain.model.ship.EscortShipGroup;
import com.supergroup.kos.building.domain.model.ship.MotherShip;
import com.supergroup.kos.building.domain.repository.cache.seamap.SeaElementConfigCacheRepository;
import com.supergroup.kos.building.domain.repository.persistence.battle.BattleProfileRepository;
import com.supergroup.kos.building.domain.repository.persistence.battle.BattleReportRepository;
import com.supergroup.kos.building.domain.repository.persistence.battle.BattleRepository;
import com.supergroup.kos.building.domain.repository.persistence.profile.KosProfileRepository;
import com.supergroup.kos.building.domain.repository.persistence.seamap.MoveSessionRepository;
import com.supergroup.kos.building.domain.repository.persistence.seamap.SeaElementConfigRepository;
import com.supergroup.kos.building.domain.repository.persistence.seamap.SeaElementRepository;
import com.supergroup.kos.building.domain.repository.persistence.ship.EscortShipGroupRepository;
import com.supergroup.kos.building.domain.repository.persistence.ship.EscortShipRepository;
import com.supergroup.kos.building.domain.repository.persistence.ship.MotherShipRepository;
import com.supergroup.kos.building.domain.service.config.KosConfigService;
import com.supergroup.kos.building.domain.service.profile.KosProfileService;
import com.supergroup.kos.building.domain.service.queen.QueenService;
import com.supergroup.kos.building.domain.service.relic.RelicService;
import com.supergroup.kos.building.domain.service.seamap.MapService;
import com.supergroup.kos.building.domain.service.seamap.RefreshNpcAndMineService;
import com.supergroup.kos.building.domain.service.seamap.SeaElementService;
import com.supergroup.kos.building.domain.service.seamap.SeaMapRefreshTransactionService;
import com.supergroup.kos.building.domain.service.seamap.ValidUserBaseService;
import com.supergroup.kos.building.domain.service.seamap.activity.SeaActivityService;
import com.supergroup.kos.building.domain.utils.DistanceUtils;
import com.supergroup.kos.building.domain.utils.SeaMapCoordinatesUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@ShellComponent
@RequiredArgsConstructor
@Slf4j
public class AdminCommand {

    private final AdminAccountService                          adminAccountService;
    private final MapService                                   mapService;
    private final SeaElementRepository<SeaElement>             elementPersistenceRepository;
    private final KosProfileService                            kosProfileService;
    private final UserRepository                               userRepository;
    private final SeaElementService                            seaElementService;
    private final EscortShipGroupRepository                    escortShipGroupRepository;
    private final EscortShipRepository                         escortShipRepository;
    private final KosProfileRepository                         kosProfileRepository;
    private final SeaActivityService                           activityService;
    private final BattleRepository                             battleRepository;
    private final BattleProfileRepository                      battleProfileRepository;
    private final BattleReportRepository                       battleReportRepository;
    private final MoveSessionRepository                        moveSessionRepository;
    private final KosConfigService                             kosConfigService;
    private final RefreshNpcAndMineService                     refreshNpcAndMineService;
    private final SeaMapRefreshTransactionService              seaMapRefreshTransactionService;
    private final ValidUserBaseService                         validUserBaseService;
    private final RelicService                                 relicService;
    private final QueenService                                 queenService;
    private final SeaElementConfigRepository<SeaElementConfig> seaElementConfigRepository;
    private final SeaElementConfigCacheRepository              seaElementConfigCacheRepository;
    private final SeaElementMapper                             seaElementMapper;
    private final MotherShipRepository                         motherShipRepository;

    @ShellMethod("Create new admin account")
    public void createAdminAccount(
            @ShellOption({ "-u", "--username" }) String username,
            @ShellOption({ "-p", "--password" }) String password) {
        adminAccountService.create(new CreateAdminAccountCommand(username, password));
        log.debug("Username: " + username);
        log.debug("Password: " + password);
    }

    @ShellMethod("Add ship")
    public void addShipToKosProfile(@ShellOption({ "-p", "--profileId" }) Long profileId) {
        var kosProfile = kosProfileRepository.findById(profileId).orElseThrow(() -> KOSException.of(ErrorCode.KOS_PROFILE_NOT_FOUND));
        var escortShipGroup = new EscortShipGroup().setEscortShipGroupLevelConfig(new EscortShipGroupLevelConfig().setId(1L))
                                                   .setAssets(kosProfile.getAssets());
        escortShipGroup = escortShipGroupRepository.save(escortShipGroup);
        for (int i = 1; i < 9; i++) {
            var config = new EscortShipConfig();
            config.setId((long) i);
            var ship = new EscortShip().setLevel(1L)
                                       .setAmount(100L)
                                       .setEscortShipGroup(escortShipGroup)
                                       .setEscortShipConfig(config)
                                       .setPercentRssBuild(1.0)
                                       .setPercentSpeedBuild(1.0)
                                       .setMaxLevel(1L);
            escortShipRepository.save(ship);
        }
    }

    @ShellMethod("Sync up map from database to cache")
    @Transactional
    public void syncUpMapFromDatabaseToCache() throws JsonProcessingException, InterruptedException {

        // save config
        log.info("Sync config to cache");
        var elementConfigs = seaElementConfigRepository.findAll();
        for (SeaElementConfig config : elementConfigs) {
            log.info("Sea element config {}", config.getId());
            var cache = seaElementMapper.toCache(config);
            seaElementConfigCacheRepository.save(cache);
        }

        var executor = Executors.newFixedThreadPool(4);
        var list = elementPersistenceRepository.findAll();
        AtomicLong done = new AtomicLong(0L);
        var size = list.size();
        var fragment = size / 4;
        var list1 = list.subList(0, 1 * fragment);
        var list2 = list.subList(1 * fragment, 2 * fragment);
        var list3 = list.subList(2 * fragment, 3 * fragment);
        var list4 = list.subList(3 * fragment, 4 * fragment);
        executor.submit(() -> {
            try {
                for (SeaElement seaElement : list1) {
                    try {
                        seaElementService.saveToCache(seaElement);
                        done.getAndIncrement();
                        log.info("Done element {}", seaElement.getId());
                        log.info("Done {}/{}", done, size);
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        executor.submit(() -> {
            try {
                for (SeaElement seaElement : list2) {
                    try {
                        seaElementService.saveToCache(seaElement);
                        done.getAndIncrement();
                        log.info("Done element {}", seaElement.getId());
                        log.info("Done {}/{}", done, size);
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        executor.submit(() -> {
            try {
                for (SeaElement seaElement : list3) {
                    try {
                        seaElementService.saveToCache(seaElement);
                        done.getAndIncrement();
                        log.info("Done element {}", seaElement.getId());
                        log.info("Done {}/{}", done, size);
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        executor.submit(() -> {
            try {
                for (SeaElement seaElement : list4) {
                    try {
                        seaElementService.saveToCache(seaElement);
                        done.getAndIncrement();
                        log.info("Done element {}", seaElement.getId());
                        log.info("Done {}/{}", done, size);
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        while (done.get() < size) {
            Thread.sleep(100);
        }
    }

    @ShellMethod("Sync sea activity")
    @Transactional
    public void syncUpSeaActivity() {
        List<SeaActivity> movingObjects = activityService.getAllMovingActivity();
        for (int i = 0; i < movingObjects.size(); i++) {
            var activity = movingObjects.get(i);
            ShipElement shipElement = activity.getShipElement();
            if (Objects.isNull(shipElement)) {continue;}
            Long elapsedTime = ChronoUnit.SECONDS.between(shipElement.getStartTime(),
                                                          LocalDateTime.now());
            long expectedTravellingDuration = (long) (DistanceUtils.getDistance(shipElement.getStart(), shipElement.getEnd())
                                                      / shipElement.getSpeed());
            Coordinates currentPosition = SeaMapCoordinatesUtils.getCurrentLocation(shipElement.getStart(), shipElement.getEnd(),
                                                                                    shipElement.getSpeed(),
                                                                                    Math.min(elapsedTime, expectedTravellingDuration));
            shipElement.setCoordinate(currentPosition);
            shipElement.setKosProfile(activity.getKosProfile());
            mapService.saveOrUpdateElement(new SaveOrUpdateElementCommand(shipElement));
            log.info("Done {}/{}", i, movingObjects.size());
        }
    }

    @ShellMethod("Init userbase for all user")
    public void initUserBaseForAllUser() throws InterruptedException {

        var executor = Executors.newFixedThreadPool(4);

        var list = userRepository.findAll();
        AtomicInteger done = new AtomicInteger();
        var size = list.size();
        var fragment = size / 4;
        var list1 = list.subList(0, 1 * fragment);
        var list2 = list.subList(1 * fragment, 2 * fragment);
        var list3 = list.subList(2 * fragment, 3 * fragment);
        var list4 = list.subList(3 * fragment, 4 * fragment);
        executor.submit(() -> {
            try {
                for (var user : list1) {
                    kosProfileService.createNewProfile(new UserCommand().setUserId(user.getId()));
                    done.getAndIncrement();
                    log.info("Done user {}", user.getId());
                    log.info("Done {}/{}", done, size);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        executor.submit(() -> {
            try {
                for (var user : list2) {
                    kosProfileService.createNewProfile(new UserCommand().setUserId(user.getId()));
                    done.getAndIncrement();
                    log.info("Done user {}", user.getId());
                    log.info("Done {}/{}", done, size);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        executor.submit(() -> {
            try {
                for (var user : list3) {
                    kosProfileService.createNewProfile(new UserCommand().setUserId(user.getId()));
                    done.getAndIncrement();
                    log.info("Done user {}", user.getId());
                    log.info("Done {}/{}", done, size);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        executor.submit(() -> {
            try {
                for (var user : list4) {
                    kosProfileService.createNewProfile(new UserCommand().setUserId(user.getId()));
                    done.getAndIncrement();
                    log.info("Done user {}", user.getId());
                    log.info("Done {}/{}", done, size);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        while (done.get() < size) {
            Thread.sleep(100);
        }
    }

    @ShellMethod("add temp battle")
    @Transactional
    public void addBattle(@ShellOption({ "-u", "--userId" }) Long userId) {
        var kosProfile1 = kosProfileService.createNewProfile(new UserCommand().setUserId(userId));
        var kosProfile2 = kosProfileService.createNewProfile(new UserCommand().setUserId(userId + 1));
        var battle = new Battle();
        var rounds = List.of(new BattleRound().setBattle(battle), new BattleRound().setBattle(battle));
        battle.setStatus(BattleStatus.PROGRESS)
              .setBattleType(BattleType.ATTACK)
              .setBattleRounds(rounds);
        battleRepository.save(battle);

        var battleMoveSession = new UserBaseMoveSession();
        battleMoveSession
                .setSpeed(1D)
                .setStart(kosProfile1.getBase().getCoordinates())
                .setEnd(kosProfile2.getBase().getCoordinates())
                .setTimeStart(LocalDateTime.now());

        battleMoveSession = moveSessionRepository.save(battleMoveSession);

        var battleReport = new BattleReport();
        battleReport.setBattle(battle);

        new BattleProfile().setBattle(battle)
                           .setBattleReport(battleReport)
                           .setUsername("Test user 1")
                           .setKosProfile(kosProfile1);

        var joiner = List.of(
                new BattleProfile().setBattle(battle)
                                   .setBattleReport(battleReport)
                                   .setUsername(kosProfile2.getUser().getUserProfile().getUsername())
                                   .setCoordinates(kosProfile2.getBase().getCoordinates())
                                   .setAvatar(kosProfile2.getUser().getUserProfile().getAvatarUrl())
                                   .setFaction(FactionType.ATTACKER)
                                   .setKosProfile(kosProfile2),
                new BattleProfile().setBattle(battle)
                                   .setBattleReport(battleReport)
                                   .setUsername(kosProfile1.getUser().getUserProfile().getUsername())
                                   .setAvatar(kosProfile1.getUser().getUserProfile().getAvatarUrl())
                                   .setCoordinates(kosProfile1.getBase().getCoordinates())
                                   .setFaction(FactionType.DEFENDER)
                                   .setKosProfile(kosProfile1));
        joiner = battleProfileRepository.saveAll(joiner);

        battle.setAttacker(joiner.get(0));
        battle.setDefender(joiner.get(1));

        battleReport.setJoiners(joiner);

        battleReport.setInitiator(joiner.get(0));
        battleReport.setVictim(joiner.get(1));
        battleReport.setStartAt(LocalDateTime.now());

        battleReport.setRoundReports(List.of(createRoundReport(joiner, battleReport, rounds.get(0), FactionType.ATTACKER),
                                             createRoundReport(joiner, battleReport, rounds.get(1), FactionType.DEFENDER)));
        battleReportRepository.save(battleReport);
    }

    @ShellMethod("Valid boss status")
    @Transactional
    public void validBossStatus() {
        var bosses = seaElementService.getAllRevivingBossSea();
        for (BossSea boss : bosses) {
            mapService.reviveBoss(boss);
        }
    }

    private RoundReport createRoundReport(List<BattleProfile> joiner, BattleReport battleReport, BattleRound battleRound, FactionType factionType) {
        var roundReport = new RoundReport();
        var escortShipReports = new ArrayList<EscortShipReport>();
        for (int i = 1; i <= 9; i++) {
            var e = new EscortShipReport(factionType);
            e.setRoundReport(roundReport);
            e.setModelId((long) i);
            e.setEscortShipType(EscortShipType.values()[i - 1]);
            e.setLeft(i * 10L);
            e.setLost(100 - e.getLeft());
            escortShipReports.add(e);
        }
        var reserveEscortShipReports = new ArrayList<ReverseEscortShipReport>();
        for (int i = 1; i <= 9; i++) {
            var e = new ReverseEscortShipReport(factionType);
            e.setModelId((long) i);
            e.setRoundReport(roundReport);
            e.setShipType(EscortShipType.values()[i - 1]);
            e.setLeft(i * 10L);
            reserveEscortShipReports.add(e);
        }
        var motherShipReports1 = new ArrayList<MotherShipReport>();
        for (int i = 1; i <= 9; i++) {
            var m = new MotherShipReport(factionType);
            m.setBattleProfile(joiner.get(0)).setRoundReport(roundReport);
            m.setModelId((long) i);
            m.setHpLost(10L)
             .setCurrentHp(90L)
             .setMaxHp(100L);
            motherShipReports1.add(m);
        }
        var reserveMotherShipReports1 = new ArrayList<ReverseMotherShipReport>();
        for (int i = 1; i <= 9; i++) {
            var m = new ReverseMotherShipReport(factionType);
            m.setModelId((long) i);
            m.setBattleProfile(joiner.get(0)).setRoundReport(roundReport);
            reserveMotherShipReports1.add(m);
        }
        var motherShipReports2 = new ArrayList<MotherShipReport>();
        for (int i = 1; i <= 9; i++) {
            var m = new MotherShipReport(factionType);
            m.setBattleProfile(joiner.get(1)).setRoundReport(roundReport);
            m.setModelId((long) i);
            m.setHpLost(10L)
             .setCurrentHp(90L)
             .setMaxHp(100L);
            motherShipReports1.add(m);
        }
        var reserveMotherShipReports2 = new ArrayList<ReverseMotherShipReport>();
        for (int i = 1; i <= 9; i++) {
            var m = new ReverseMotherShipReport(factionType);
            m.setModelId((long) i);
            m.setBattleProfile(joiner.get(1)).setRoundReport(roundReport);
            reserveMotherShipReports1.add(m);
        }
        roundReport.setBattleReport(battleReport)
                   .setRound(battleRound)
                   .setAttackerDamageReport(new DamageReport().setArmour(0L)
                                                              .setDodge(0L)
                                                              .setEscortShip(100L)
                                                              .setFirePower(100L)
                                                              .setEscortShipLost(0L)
                                                              .setFireResistance(100L)
                                                              .setHeathPoint(100L)
                                                              .setMotherShipLost(0L)
                                                              .setMotherShip(1L)
                                                              .setPhysicalAttack(100L)
                                                              .setTakenFirePower(100L)
                                                              .setTakenPhysicalAttack(100L))
                   .setAttackerUsedItem(List.of(new RoundUsedItem().setBattleProfile(joiner.get(0))
                                                                   .setRoundReport(roundReport)
                                                                   .setItems(List.of(new Item().setId(ItemId.WA_9), new Item().setId(ItemId.WA_13)))))
                   .setAttackerEscortShipReports(escortShipReports)
                   .setAttackerReserveEscortShipReports(reserveEscortShipReports)
                   .setAttackerMotherShipReports(motherShipReports1)
                   .setAttackerReserveMotherShipReports(reserveMotherShipReports1)
                   .setDefenderDamageReport(new DamageReport().setArmour(0L)
                                                              .setDodge(0L)
                                                              .setEscortShip(100L)
                                                              .setFirePower(100L)
                                                              .setEscortShipLost(0L)
                                                              .setFireResistance(100L)
                                                              .setHeathPoint(100L)
                                                              .setMotherShipLost(0L)
                                                              .setMotherShip(1L)
                                                              .setPhysicalAttack(100L)
                                                              .setTakenFirePower(100L)
                                                              .setTakenPhysicalAttack(100L))
                   .setDefenderUsedItem(List.of(new RoundUsedItem().setBattleProfile(joiner.get(0))
                                                                   .setRoundReport(roundReport)
                                                                   .setItems(List.of(new Item().setId(ItemId.WA_9), new Item().setId(ItemId.WA_13)))))
                   .setDefenderEscortShipReports(escortShipReports)
                   .setDefenderReserveEscortShipReports(reserveEscortShipReports)
                   .setDefenderMotherShipReports(motherShipReports2)
                   .setDefenderReserveMotherShipReports(reserveMotherShipReports2);
        return roundReport;
    }

    @ShellMethod("delete all battle")
    @Transactional
    public void deleteAllBattle(@ShellOption({ "-u", "--userId" }) Long userId) {
        var kosProfile = kosProfileService.createNewProfile(new UserCommand().setUserId(userId));
        var listBattle = battleRepository.findByKosProfileId(kosProfile.getId());
        battleRepository.deleteAll(listBattle);
    }

    @ShellMethod("validate arrival main base time")
    @Transactional
    public void validateArrivalMainBaseTime() {
        var motherShips = motherShipRepository.findAll();
        for (MotherShip motherShip : motherShips) {
            try {
                if (motherShip.getStatus().equals(SeaActivityStatus.STANDBY)) {
                    motherShip.setCurrentHp(motherShip.getMaxHp().longValue());
                    motherShip.setArrivalMainBaseTime(LocalDateTime.now());
                    motherShip.setLastTimeCalculateHp(null);
                    motherShipRepository.save(motherShip);
                } else {
                    motherShip.setArrivalMainBaseTime(null);
                    motherShip.setLastTimeCalculateHp(null);
                    motherShipRepository.save(motherShip);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                // ignore
            }
        }
    }

    @ShellMethod("set up test case for mother ship hp recovery")
    @Transactional
    public void setUpTestCaseMotherShipHealing(@ShellOption({ "-k", "--kosProfileId" }) Long kosProfileId) {
        var motherShips = motherShipRepository.findByKosProfileId(kosProfileId);
        var standByMotherShip = motherShips.stream()
                                           .filter(m -> m.getStatus().equals(SeaActivityStatus.STANDBY))
                                           .collect(Collectors.toList());
        standByMotherShip.forEach(m -> m.setCurrentHp((m.getId() % 4) * 10)
                                        .setLastTimeCalculateHp(null)
                                        .setArrivalMainBaseTime(LocalDateTime.now()));
        motherShipRepository.saveAll(standByMotherShip);
    }

    @ShellMethod("refresh mine and npc")
    @Transactional
    public void refreshNpcAndMine(@ShellOption({ "-u", "--userId" }) Long userId) throws InterruptedException {
        try {
            log.info("Refresh NPC and Mine");
            // valid BaseUser not ready
            validUserBaseService.validBaseUser();

            List<Time> listTimeRefresh = kosConfigService.getSeaMapRefreshConfig().getListTimeRefresh();
            Optional<SeaMapRefreshTransaction> optional = seaMapRefreshTransactionService.findLastRefreshTransaction();
            LocalDateTime now = LocalDateTime.now();
            if (isRefresh(listTimeRefresh, optional, now)) {
                RefreshNpcAndMineResult result = refreshNpcAndMineService.refreshNpcAndMine();
                createSeaMapRefreshTransaction(result, now);
            }
            log.info("Refresh NPC and Mine done!");
        } catch (Exception e) {
            if (e instanceof KOSException) {
                e.printStackTrace();
            } else {
                throw e;
            }
        }
    }

    @ShellMethod("validate mp")
    @Transactional
    public void validateMp() {
        var kosProfiles = kosProfileRepository.findAll();
        for (KosProfile kosProfile : kosProfiles) {
            log.info("Process {}/{}", kosProfiles.indexOf(kosProfile), kosProfiles.size());
            try {
                var mpQueen = queenService.getMpFromQueens(new GetMpFromQueenCommand().setKosProfile(kosProfile).setIgnoreCheckOccupy(true));
                var mpRelic = relicService.getMpFromRelicListings(
                        new GetMpFromRelicCommand().setKosProfileId(kosProfile.getId()).setIgnoreCheckOccupy(true));
                var mpCastle = kosProfileService.getInitAssetConfig().getMp();
                kosProfile.getPoint().setMpPoint(mpQueen + mpRelic + mpCastle);
            } catch (Exception exception) {
                exception.printStackTrace();
                // ignore
            }
        }
    }

    private void createSeaMapRefreshTransaction(RefreshNpcAndMineResult result, LocalDateTime now) {
        SeaMapRefreshTransaction seaMapRefreshTransaction = new SeaMapRefreshTransaction();
        seaMapRefreshTransaction.setTimeRefresh(now)
                                .setTotalElementDeleted(result.getTotalElementDeleted())
                                .setTotalElementAccordingBaseCreated(result.getTotalElementAccordingBaseCreated())
                                .setTotalElementAccordingZoneSeaCreated(result.getTotalElementAccordingZoneSeaCreated());
        seaMapRefreshTransactionService.save(seaMapRefreshTransaction);
    }

    private Boolean isRefresh(List<Time> listTimeRefresh, Optional<SeaMapRefreshTransaction> optional, LocalDateTime now) {
        if (optional.isEmpty()) {
            return true;
        }

        LocalDateTime lastTimeRefreshDateTime = optional.get().getTimeRefresh();
        Time lastRefreshTime = Time.valueOf(lastTimeRefreshDateTime.toLocalTime());
        Time timeNow = Time.valueOf(now.toLocalTime());
        boolean isDaysEarlier = lastTimeRefreshDateTime.toLocalDate().isBefore(now.toLocalDate());
        for (Time timeRefresh : listTimeRefresh) {
            if (timeNow.after(timeRefresh) && (isDaysEarlier || lastRefreshTime.before(timeRefresh))) {
                return true;
            }
        }
        return false;
    }
}

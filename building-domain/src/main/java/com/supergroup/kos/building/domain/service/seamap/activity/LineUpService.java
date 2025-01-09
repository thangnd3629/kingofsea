package com.supergroup.kos.building.domain.service.seamap.activity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.supergroup.core.constant.ErrorCode;
import com.supergroup.core.exception.KOSException;
import com.supergroup.kos.building.domain.command.GetEscortShipCommand;
import com.supergroup.kos.building.domain.command.GetMotherShipCommand;
import com.supergroup.kos.building.domain.command.PrepareShipLineupCommand;
import com.supergroup.kos.building.domain.constant.EscortShipType;
import com.supergroup.kos.building.domain.constant.WeaponStat;
import com.supergroup.kos.building.domain.constant.battle.FactionType;
import com.supergroup.kos.building.domain.constant.battle.ShipStatisticType;
import com.supergroup.kos.building.domain.constant.seamap.SeaActivityStatus;
import com.supergroup.kos.building.domain.dto.seamap.EscortSquadDTO;
import com.supergroup.kos.building.domain.model.config.MotherShipConfigQualityConfig;
import com.supergroup.kos.building.domain.model.profile.KosProfile;
import com.supergroup.kos.building.domain.model.seamap.EscortShipSquad;
import com.supergroup.kos.building.domain.model.seamap.KosWarInfo;
import com.supergroup.kos.building.domain.model.seamap.SeaActivity;
import com.supergroup.kos.building.domain.model.seamap.ShipLineUp;
import com.supergroup.kos.building.domain.model.seamap.reward.LoadedOnShipReward;
import com.supergroup.kos.building.domain.model.ship.EscortShip;
import com.supergroup.kos.building.domain.model.ship.MotherShip;
import com.supergroup.kos.building.domain.repository.persistence.seamap.LineUpRepository;
import com.supergroup.kos.building.domain.service.seamap.KosWarInfoService;
import com.supergroup.kos.building.domain.service.ship.EscortShipService;
import com.supergroup.kos.building.domain.service.ship.MotherShipService;
import com.supergroup.kos.notification.domain.service.NotificationTemplateService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LineUpService {
    private final LineUpRepository            repository;
    private final EscortShipService           escortShipService;
    private final MotherShipService           motherShipService;
    private final KosWarInfoService           kosWarInfoService;
    private final NotificationTemplateService notificationTemplateService;

    public ShipLineUp save(ShipLineUp shipLineUp) {
        return repository.save(shipLineUp);
    }

    public List<ShipLineUp> saveAll(List<ShipLineUp> shipLineUps) {
        return repository.saveAll(shipLineUps);
    }

    @Transactional
    public ShipLineUp updateLineUp(PrepareShipLineupCommand command) {
        Long kosProfileId = command.getKosProfileId();
        MotherShip motherShip = motherShipService.getMotherShipById(new GetMotherShipCommand()
                                                                            .setMotherShipId(command.getMotherShipId())
                                                                            .setKosProfileId(kosProfileId));
        ShipLineUp lineUp = new ShipLineUp();
        List<ShipLineUp> latestModifiedLineUps = getLatestLineUp(kosProfileId, motherShip.getId(), PageRequest.of(0, 1)).toList();
        if (!latestModifiedLineUps.isEmpty() && Objects.isNull(latestModifiedLineUps.get(0).getMotherShip())) {
            lineUp = latestModifiedLineUps.get(0);
        }
        if (Objects.nonNull(lineUp.getActiveMotherShip())) {
            throw KOSException.of(ErrorCode.LINE_UP_ALREADY_ON_MISSION);
        }
        Long carriedShips = 0L;
        List<EscortShipSquad> escortShipSquads = new ArrayList<>();
        lineUp.setEscortShipSquad(escortShipSquads);

        long commandStatWeapon = KosWarInfoService.getWeaponPower(motherShip.getWeapons(), WeaponStat.CMD).longValue();
        long commandStatWeaponSet = KosWarInfoService.getWeaponSetPower(motherShip.getWeaponSets(), WeaponStat.CMD).longValue();

        long commandStat = (long) (motherShip.getMotherShipConfigQualityConfig().getMotherShipConfig().getCmd() *
                                   motherShip.getMotherShipConfigQualityConfig().getMotherShipQualityConfig().getPercentStat() *
                                   motherShip.getMotherShipLevelConfig().getPercentStat())
                           + commandStatWeapon + commandStatWeaponSet;

        lineUp.setMotherShip(motherShip);

        for (EscortSquadDTO escortSquad : command.getEscortShips()) {
            EscortShip escortShips = escortShipService.getEscortShipByShipType(new GetEscortShipCommand().setKosProfileId(kosProfileId)
                                                                                                         .setShipType(
                                                                                                                 escortSquad.getEscortShipType()));
            Long availableAmount = escortShips.getAmount();
            Long requestedAmount = escortSquad.getAmount();
            if (availableAmount < requestedAmount) {
                throw KOSException.of(ErrorCode.NOT_ENOUGH_AMOUNT_OF_ESCORT_SHIP);
            }
            carriedShips += requestedAmount;
            if (carriedShips > commandStat) {
                throw KOSException.of(ErrorCode.MOTHER_SHIP_CAPACITY_EXCEEDED);
            }
            EscortShipSquad shipSquad = new EscortShipSquad()
                    .setEscortShip(escortShips)
                    .setAmount(requestedAmount)
                    .setInitialAmount(requestedAmount);
            shipSquad.setLineUp(lineUp);
            lineUp.getEscortShipSquad().add(shipSquad);
        }
        lineUp.setEscortShipUnits(carriedShips);
        repository.save(lineUp);
        return lineUp;
    }

    @Transactional
    public ShipLineUp attachToActivity(SeaActivity activity, ShipLineUp lineUp) {
        MotherShip motherShip = lineUp.getMotherShip();
        // check if line up is moved
        if (Objects.nonNull(lineUp.getActivity())) {
            throw KOSException.of(ErrorCode.LINE_UP_WAS_ALREADY_DEPLOYED);
        }
        // check if mother ship is active on other line up
        if (Objects.nonNull(motherShip.getActiveLineUp())) {
            throw KOSException.of(ErrorCode.MOTHER_SHIP_ALREADY_ON_MISSION);
        }
        // set up reward storage on ship
        LoadedOnShipReward loadedOnShipReward = new LoadedOnShipReward();
        loadedOnShipReward.setActivity(activity).setTonnage(KosWarInfoService.getMotherShipPower(motherShip, ShipStatisticType.TONNAGE));
        activity.setLoadedOnShipReward(loadedOnShipReward);
        // set arrival main base time is null when mother ship is moving
        motherShip.setArrivalMainBaseTime(null);
        motherShipService.save(motherShip);

        lineUp.setActiveMotherShip(motherShip);

        // check amount of escort ship
        Long kosProfileId = activity.getKosProfile().getId();
        for (EscortShipSquad squad : lineUp.getEscortShipSquad()) {
            EscortShip escortShips = escortShipService.getEscortShipByShipType(new GetEscortShipCommand().setKosProfileId(kosProfileId)
                                                                                                         .setShipType(squad.getEscortShip()
                                                                                                                           .getEscortShipConfig()
                                                                                                                           .getType()));
            Long availableAmount = escortShips.getAmount();
            if (availableAmount < squad.getAmount()) {
                throw KOSException.of(ErrorCode.NOT_ENOUGH_AMOUNT_OF_ESCORT_SHIP);
            }
            escortShipService.decreaseAmount(escortShips, squad.getAmount());
        }

        lineUp.setActivity(activity);
        activity.setLineUp(lineUp);
        repository.save(lineUp);
        return lineUp;
    }

    public Long getCurrentShipUnitsCount(ShipLineUp lineUp) {
        Long shipUnits = 0L;
        for (EscortShipSquad escortSquad : lineUp.getEscortShipSquad()) {
            shipUnits += escortSquad.getAmount();
        }
        return shipUnits;
    }

    public Double getLineUpBaseSpeed(ShipLineUp lineUp) {
        MotherShip motherShip = lineUp.getActiveMotherShip();
        MotherShipConfigQualityConfig quality = motherShip.getMotherShipConfigQualityConfig();
        Double speed = quality.getMotherShipConfig().getSpeed() * quality.getMotherShipQualityConfig().getPercentStat() * motherShip
                .getMotherShipLevelConfig().getPercentStat();
        return speed;
    }

    public ShipLineUp getLineUpById(Long kosProfileId, Long lineUpId) {
        return repository.getKosProfileLineUp(kosProfileId, lineUpId).orElseThrow(
                () -> KOSException.of(ErrorCode.SHIP_LINE_UP_NOT_FOUND));
    }

    public List<ShipLineUp> getLineupByIdIn(List<Long> ids) {
        return repository.findByIdIn(ids);
    }

    public Page<ShipLineUp> getLatestLineUp(Long kosProfileId, Long motherShipId, Pageable pageable) {
        MotherShip motherShip = motherShipService.getMotherShipById(new GetMotherShipCommand()
                                                                            .setMotherShipId(motherShipId)
                                                                            .setKosProfileId(kosProfileId));
        return repository.findByMotherShipIdOrderByUpdatedAtDesc(motherShipId, pageable);
    }

    public List<EscortSquadDTO> getCurrentLineUp(Long kosProfileId, Long motherShipId) {
        List<ShipLineUp> latestLineUps = getLatestLineUp(kosProfileId, motherShipId, PageRequest.of(0, 1)).toList();
        List<EscortSquadDTO> currentBuildableLineUp = new ArrayList<>();
        if (latestLineUps.size() == 0) {
            return currentBuildableLineUp;
        }
        ShipLineUp lineUp = latestLineUps.get(0);
        for (EscortShipSquad squad : lineUp.getEscortShipSquad()) {
            EscortSquadDTO buildableSquad = new EscortSquadDTO();
            EscortShipType type = squad.getEscortShip().getEscortShipConfig()
                                       .getType();
            EscortShip remainingShips = escortShipService.getEscortShipByShipType(
                    new GetEscortShipCommand().setShipType(type).setKosProfileId(kosProfileId));
            buildableSquad.setEscortShipType(type);
            if (remainingShips.getAmount() >= squad.getAmount()) {
                buildableSquad.setAmount(squad.getAmount());
            } else {
                buildableSquad.setAmount(remainingShips.getAmount());
            }
            currentBuildableLineUp.add(buildableSquad);
        }
        return currentBuildableLineUp;
    }

    public ShipLineUp onFinishMission(ShipLineUp lineUp) {
        Long totalShipLost = 0L;
        KosProfile kosProfile = null;
        lineUp.setActiveMotherShip(null);
        MotherShip motherShip = lineUp.getMotherShip();
        if (Objects.nonNull(motherShip)) {
            kosProfile = motherShip.getCommandBuilding().getKosProfile();
            lineUp.getMotherShip().setStatus(SeaActivityStatus.STANDBY);
            motherShip.setCurrentHp(kosWarInfoService.getMotherShipPower(motherShip, ShipStatisticType.HP).longValue());
        }
        // regen full hp
        List<EscortShipSquad> escortShipSquads = lineUp.getEscortShipSquad();
        for (EscortShipSquad squad : escortShipSquads) {
            Long remainingShip = squad.getAmount() - squad.getKilled();
            EscortShip escortShip = squad.getEscortShip();
            escortShip.setAmount(escortShip.getAmount() + remainingShip);
            totalShipLost += squad.getKilled();
            if (kosProfile == null) {
                kosProfile = squad.getEscortShip().getEscortShipGroup().getAssets().getKosProfile();
            }
        }
        // update warshipsLost in warInfo
        if (kosProfile != null) {
            KosWarInfo kosWarInfo = kosWarInfoService.getByKosProfileId(kosProfile.getId());
            kosWarInfo.setWarshipsLost(kosWarInfo.getWarshipsLost() + totalShipLost);
            kosWarInfoService.save(kosWarInfo);
        }
        // save
        save(lineUp);
        return lineUp;
    }

    public List<ShipLineUp> getListShipLineupWaitingInBattle(Long battleId, FactionType factionType, LocalDateTime time) {
        return repository.getLineupWaitingInBattle(battleId, factionType, time);
    }

    public List<ShipLineUp> findByBattleProfileIdAndTimeJoinedBattleAfter(Long battleProfileId, LocalDateTime time) {
        return repository.findByBattleProfileIdAndTimeJoinedBattleAfter(battleProfileId, time);
    }

    public List<ShipLineUp> getListShipLineupInBattleByFaction(Long battleId, FactionType factionType) {
        return repository.getListLineupInBattleByFaction(battleId, factionType);
    }

    public List<ShipLineUp> findByBattleId(Long battleId) {
        return repository.findByBattleId(battleId);
    }

    @Transactional(readOnly = true)
    public Double getPowerLineup(ShipLineUp shipLineUp) {
        Double totalPower = 0D;
        // power in motherShip
        if (Objects.nonNull(shipLineUp.getMotherShip())) {
            MotherShip motherShip = shipLineUp.getMotherShip();
            KosProfile kosProfile = motherShip.getCommandBuilding().getKosProfile();

            totalPower += kosWarInfoService.getMotherShipPower(shipLineUp.getMotherShip(), ShipStatisticType.ATK1);
            List<EscortSquadDTO> lineUpDTOS = getCurrentLineUp(kosProfile.getId(), motherShip.getId());
            for (EscortSquadDTO squad : lineUpDTOS) {
                EscortShip escortShip = escortShipService.getEscortShipByShipType(
                        new GetEscortShipCommand().setShipType(squad.getEscortShipType()).setKosProfileId(kosProfile.getId()));
                totalPower += kosWarInfoService.getSingleEscortShipPower(escortShip, ShipStatisticType.ATK1) * squad.getAmount();
            }
        }

        return totalPower;
    }
}

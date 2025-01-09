package com.supergroup.kos.building.domain.service.ship;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.supergroup.core.constant.ErrorCode;
import com.supergroup.core.exception.KOSException;
import com.supergroup.kos.building.domain.async.ShipUpgradeAsyncTask;
import com.supergroup.kos.building.domain.command.GetArmoryBuildingInfoCommand;
import com.supergroup.kos.building.domain.command.KosProfileCommand;
import com.supergroup.kos.building.domain.command.UpgradeEscortShipGroupCommand;
import com.supergroup.kos.building.domain.constant.EscortShipGroupLevel;
import com.supergroup.kos.building.domain.model.asset.Assets;
import com.supergroup.kos.building.domain.model.config.EscortShipGroupLevelConfig;
import com.supergroup.kos.building.domain.model.profile.KosProfile;
import com.supergroup.kos.building.domain.model.ship.EscortShip;
import com.supergroup.kos.building.domain.model.ship.EscortShipGroup;
import com.supergroup.kos.building.domain.repository.persistence.ship.EscortShipGroupLevelConfigRepository;
import com.supergroup.kos.building.domain.repository.persistence.ship.EscortShipGroupRepository;
import com.supergroup.kos.building.domain.repository.persistence.ship.EscortShipLevelConfigDataSource;
import com.supergroup.kos.building.domain.service.asset.AssetsService;
import com.supergroup.kos.building.domain.service.building.ArmoryBuildingService;
import com.supergroup.kos.building.domain.service.profile.KosProfileService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EscortShipGroupService {

    private final EscortShipGroupRepository            escortShipGroupRepository;
    private final AssetsService                        assetsService;
    private final ArmoryBuildingService                armoryBuildingService;
    private final EscortShipLevelConfigDataSource      escortShipLevelConfigDataSource;
    private final EscortShipGroupLevelConfigRepository escortShipGroupLevelConfigRepository;
    private final ShipUpgradeAsyncTask                 shipUpgradeAsyncTask;
    private final KosProfileService                    kosProfileService;

    public List<EscortShipGroup> getEscortShipGroups(Long kosProfileId) {
        var escortShipGroups = escortShipGroupRepository.findByKosProfileIdOrderByEscortShipGroupNameAsc(kosProfileId);
        return escortShipGroups.stream().peek(group -> {
            var percentQualityStat = group.getEscortShipGroupLevelConfig().getPercentStat();
            var escortShips = group.getEscortShips().stream().sorted(Comparator.comparing(EscortShip::getId)).collect(Collectors.toList())
                                   .stream().peek(escortShip -> {
                        var percentLevelStat = escortShipLevelConfigDataSource.getByTypeAndLevel(escortShip.getEscortShipConfig().getType(),
                                                                                                 escortShip.getLevel()).getPercentStat();
                        escortShip.setPercentLevelStat(percentLevelStat)
                                  .setPercentQualityStat(percentQualityStat);
                    }).collect(Collectors.toList());
            group.setEscortShips(escortShips);
        }).collect(Collectors.toList());
    }

    @Transactional
    public void upgrade(UpgradeEscortShipGroupCommand command) {
        KosProfile kosProfile = kosProfileService.getKosProfileById(command.getKosProfileId());
        var escortShipGroup = escortShipGroupRepository.findKosProfileIdAndEscortShipGroupConfigName(command.getKosProfileId(), command.getGroup())
                                                       .orElseThrow(() -> KOSException.of(ErrorCode.ESCORT_SHIP_GROUP_IS_NOT_FOUND));
        var assets = assetsService.getAssets(new KosProfileCommand().setKosProfileId(command.getKosProfileId()));
        var armoryBuilding = armoryBuildingService.getBuildingInfo(new GetArmoryBuildingInfoCommand(command.getKosProfileId()));
        var level = escortShipGroup.getEscortShipGroupLevelConfig().getLevel();
        if (level.equals(EscortShipGroupLevel.TITAN)) {
            throw KOSException.of(ErrorCode.CAN_NOT_UPGRADE_BECAUSE_THE_LEVEL_IS_MAXED);
        }
        var nextLevel = EscortShipGroupLevel.getNextLevel(level);
        var nextLevelConfig = escortShipGroupLevelConfigRepository.findByGroupNameAndGroupLevel(command.getGroup(), nextLevel)
                                                                  .orElseThrow(() -> KOSException.of(
                                                                          ErrorCode.ESCORT_SHIP_GROUP_LEVEL_CONFIG_IS_NOT_FOUND));
        Double nextStatGrowthRate = nextLevelConfig.getPercentStat();
        if (!checkResourceRequirement(nextLevelConfig, assets)) {
            throw KOSException.of(ErrorCode.DO_NOT_MEET_RESOURCE_REQUIREMENT);
        }
        if (Objects.nonNull(nextLevelConfig.getArmoryBuildingConfig())
            && nextLevelConfig.getArmoryBuildingConfig().getLevel() > armoryBuilding.getLevel()) {
            throw KOSException.of(ErrorCode.ARMORY_BUILDING_IS_NOT_LEVEL_ENOUGH);
        }
        escortShipGroup.setEscortShipGroupLevelConfig(nextLevelConfig);
        takeAssetToUpgrade(nextLevelConfig, assets);

        shipUpgradeAsyncTask.sendGuardShipUpgradeQualityNotification(kosProfile.getUser().getId(),
                                                                     escortShipGroup.getEscortShipGroupLevelConfig().getEscortShipGroupConfig()
                                                                                    .getName().toString(), nextLevelConfig, nextStatGrowthRate);
        escortShipGroupRepository.save(escortShipGroup);
    }

    private Boolean checkResourceRequirement(EscortShipGroupLevelConfig escortShipGroupLevelConfig, Assets assets) {
        return assets.getWood() >= escortShipGroupLevelConfig.getWood()
               && assets.getGold() >= escortShipGroupLevelConfig.getGold()
               && assets.getStone() >= escortShipGroupLevelConfig.getStone();
    }

    private void takeAssetToUpgrade(EscortShipGroupLevelConfig escortShipGroupLevelConfig, Assets assets) {
        assets.setGold(assets.getGold() - escortShipGroupLevelConfig.getGold());
        assets.setStone(assets.getStone() - escortShipGroupLevelConfig.getStone());
        assets.setWood(assets.getWood() - escortShipGroupLevelConfig.getWood());
        assetsService.save(assets);
    }

}

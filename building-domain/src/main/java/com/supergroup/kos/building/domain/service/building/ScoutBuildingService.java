package com.supergroup.kos.building.domain.service.building;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.supergroup.core.constant.ErrorCode;
import com.supergroup.core.exception.KOSException;
import com.supergroup.kos.building.domain.command.GetScoutBuildingInfoCommand;
import com.supergroup.kos.building.domain.command.GetScoutCaseConfigCommand;
import com.supergroup.kos.building.domain.command.KosProfileCommand;
import com.supergroup.kos.building.domain.command.ScoutTrainingCommand;
import com.supergroup.kos.building.domain.constant.BuildingName;
import com.supergroup.kos.building.domain.constant.MissionStatus;
import com.supergroup.kos.building.domain.model.asset.Assets;
import com.supergroup.kos.building.domain.model.config.BaseBuildingConfig;
import com.supergroup.kos.building.domain.model.config.ScoutBuildingConfig;
import com.supergroup.kos.building.domain.model.config.ScoutCaseConfig;
import com.supergroup.kos.building.domain.model.mining.ScoutBuilding;
import com.supergroup.kos.building.domain.repository.persistence.building.BuildingConfigDataSource;
import com.supergroup.kos.building.domain.repository.persistence.building.ScoutBuildingRepository;
import com.supergroup.kos.building.domain.repository.persistence.scout.ScoutCaseConfigRepository;
import com.supergroup.kos.building.domain.repository.persistence.scout.ScoutReportRepository;
import com.supergroup.kos.building.domain.repository.persistence.scout.ScoutRepository;
import com.supergroup.kos.building.domain.service.asset.AssetsService;
import com.supergroup.kos.building.domain.service.profile.KosProfileService;
import com.supergroup.kos.building.domain.service.technology.TechnologyService;

@Service
public class ScoutBuildingService extends BaseBuildingService {
    private final ScoutBuildingRepository   scoutBuildingRepository;
    private final ScoutCaseConfigRepository scoutCaseConfigRepository;
    private final AssetsService             assetsService;
    private final ScoutReportRepository     scoutReportRepository;
    private final TechnologyService         technologyService;
    private final ScoutRepository           scoutRepository;

    public ScoutBuildingService(@Autowired KosProfileService kosProfileService,
                                @Autowired BuildingConfigDataSource buildingConfigDataSource,
                                @Autowired ScoutBuildingRepository scoutBuildingRepository,
                                @Autowired ScoutCaseConfigRepository scoutCaseConfigRepository,
                                @Autowired AssetsService assetsService,
                                @Autowired ScoutReportRepository scoutReportRepository,
                                @Autowired TechnologyService technologyService, ScoutRepository scoutRepository) {
        super(kosProfileService, buildingConfigDataSource);
        this.scoutBuildingRepository = scoutBuildingRepository;
        this.scoutCaseConfigRepository = scoutCaseConfigRepository;
        this.assetsService = assetsService;
        this.scoutReportRepository = scoutReportRepository;
        this.technologyService = technologyService;
        this.scoutRepository = scoutRepository;
    }

    public ScoutBuilding save(ScoutBuilding scoutBuilding) {
        return scoutBuildingRepository.save(scoutBuilding);
    }

    public ScoutBuilding getBuildingInfo(GetScoutBuildingInfoCommand command) {
        ScoutBuilding scoutBuilding = scoutBuildingRepository.findByKosProfile_Id(command.getKosProfileId()).orElseThrow(() -> KOSException.of(
                ErrorCode.SCOUT_BUILDING_NOT_FOUND));

        if (Objects.isNull(command.getCheckUnlockBuilding()) || command.getCheckUnlockBuilding()) {
            scoutBuilding.validUnlockBuilding(technologyService);
        }

        var config = (ScoutBuildingConfig) buildingConfigDataSource.getConfig(scoutBuilding.getName(), scoutBuilding.getLevel());
        scoutBuilding = checkTrainingProcess(scoutBuilding);
        scoutBuilding.setCapacity(config.getCapacity())
                     .setNumberMission(getNumberMission(command.getKosProfileId()));
        return scoutBuilding;
    }

    public Long getNumberMission(Long kosProfileId) {
        return scoutRepository.countByScouterIdAndMissionStatus(kosProfileId, MissionStatus.DOING);
    }

    public List<ScoutCaseConfig> getScoutCaseConfigByNumberEnemy(GetScoutCaseConfigCommand command) {
        Long enemy = command.getEnemy();
        return scoutCaseConfigRepository.findByNumberEnemy(enemy);
    }

    @Transactional
    public void trainingSoldier(ScoutTrainingCommand command) {
        ScoutBuilding scoutBuilding = getBuildingInfo(new GetScoutBuildingInfoCommand(command.getKosProfileId()));
        ScoutBuildingConfig config = (ScoutBuildingConfig) getBuildingConfig(scoutBuilding.getLevel());
        if (scoutBuilding.getTotalScout() + command.getSoldier() > config.getCapacity()) {
            throw KOSException.of(ErrorCode.EXCEED_THE_MAXIMUM_NUMBER);
        }
        Assets assets = assetsService.getAssets(new KosProfileCommand().setKosProfileId(command.getKosProfileId()));
        if (!checkResourceRequirement(config, assets, command.getSoldier())) {
            throw KOSException.of(ErrorCode.DO_NOT_MEET_RESOURCE_REQUIREMENT);
        }

        if (!buildingIsAvailable(scoutBuilding)) {
            throw KOSException.of(ErrorCode.BUILDING_IS_IN_OTHER_PROCESS);
        }

        // update assets
        Assets assetsAfterTraining = updateAssets(assets, config, command.getSoldier());
        assetsService.save(assetsAfterTraining);

        // training
        scoutBuilding.setIsTraining(true)
                     .setStartTrainingTime(LocalDateTime.now())
                     .setTrainingDuration(config.getTrainingTime() * command.getSoldier())
                     .setScoutTraining(command.getSoldier());
        scoutBuildingRepository.save(scoutBuilding);
    }

    private Boolean checkResourceRequirement(ScoutBuildingConfig config, Assets assets, Long soldier) {
        return assets.getWood() >= config.getCostTrainingWood() * soldier
               && assets.getGold() >= config.getCostTrainingGold() * soldier
               && assets.getStone() >= config.getCostTrainingStone() * soldier;
    }

    private Boolean buildingIsAvailable(ScoutBuilding building) {
        return (Objects.isNull(building.getIsTraining()) || !building.getIsTraining())
               && !building.getIsLock();
    }

    private Assets updateAssets(Assets assets, ScoutBuildingConfig config, Long soldier) {
        assets.setWood(assets.getWood() - (config.getCostTrainingWood() * soldier))
              .setGold(assets.getGold() - (config.getCostTrainingGold() * soldier))
              .setStone(assets.getStone() - (config.getCostTrainingStone() * soldier));
        return assets;
    }

    private ScoutBuilding checkTrainingProcess(ScoutBuilding scoutBuilding) {
        if (Objects.nonNull(scoutBuilding.getIsTraining()) && scoutBuilding.getIsTraining()) {
            if (checkTrainingIsDone(scoutBuilding)) {
                ScoutBuildingConfig config = (ScoutBuildingConfig) getBuildingConfig(scoutBuilding.getLevel());
                Long totalScout = Math.min(config.getCapacity(), scoutBuilding.getTotalScout() + scoutBuilding.getScoutTraining());
                scoutBuilding.setAvailableScout(scoutBuilding.getAvailableScout() + totalScout - scoutBuilding.getTotalScout())
                             .setTotalScout(totalScout)
                             .setIsTraining(false)
                             .setScoutTraining(null)
                             .setStartTrainingTime(null)
                             .setTrainingDuration(null);
                return scoutBuildingRepository.save(scoutBuilding);
            }
        }
        return scoutBuilding;
    }

    private Boolean checkTrainingIsDone(ScoutBuilding scoutBuilding) {
        LocalDateTime timeDone = scoutBuilding.getStartTrainingTime().plusSeconds(scoutBuilding.getTrainingDuration() / 1000);
        return LocalDateTime.now().isAfter(timeDone);
    }

    @Override
    protected BaseBuildingConfig getBuildingConfig(Long level) {
        return buildingConfigDataSource.getConfig(BuildingName.SCOUT, level);
    }
}

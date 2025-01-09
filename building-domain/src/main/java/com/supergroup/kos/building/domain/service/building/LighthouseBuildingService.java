package com.supergroup.kos.building.domain.service.building;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.supergroup.core.constant.ErrorCode;
import com.supergroup.core.exception.KOSException;
import com.supergroup.kos.building.domain.constant.BuildingName;
import com.supergroup.kos.building.domain.model.building.LighthouseBuilding;
import com.supergroup.kos.building.domain.model.config.BaseBuildingConfig;
import com.supergroup.kos.building.domain.model.config.LighthouseBuildingConfig;
import com.supergroup.kos.building.domain.repository.persistence.building.BuildingConfigDataSource;
import com.supergroup.kos.building.domain.repository.persistence.building.LighthouseBuildingRepository;
import com.supergroup.kos.building.domain.repository.persistence.seamap.SeaActivityRepository;
import com.supergroup.kos.building.domain.service.profile.KosProfileService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class LighthouseBuildingService extends BaseBuildingService {
    private final LighthouseBuildingRepository lightHouseBuildingRepository;
    private final SeaActivityRepository        seaActivityRepository;

    public LighthouseBuildingService(@Autowired KosProfileService kosProfileService,
                                     @Autowired BuildingConfigDataSource buildingConfigDataSource,
                                     @Autowired LighthouseBuildingRepository lightHouseBuildingRepository,
                                     @Autowired SeaActivityRepository seaActivityRepository) {
        super(kosProfileService, buildingConfigDataSource);
        this.lightHouseBuildingRepository = lightHouseBuildingRepository;
        this.seaActivityRepository = seaActivityRepository;
    }

    public LighthouseBuilding getBuildingInfo(Long kosProfileId) {
        LighthouseBuilding lightHouseBuilding = lightHouseBuildingRepository.findByKosProfileId(kosProfileId)
                                                                            .orElseThrow(() -> KOSException.of(
                                                                                    ErrorCode.LIGHTHOUSE_BUILDING_IS_NOT_FOUND));
        LighthouseBuildingConfig lightHouseBuildingConfig  = (LighthouseBuildingConfig) buildingConfigDataSource.getConfig(BuildingName.LIGHTHOUSE, lightHouseBuilding.getLevel());
        lightHouseBuilding.setActionPointUsed(seaActivityRepository.countActiveActivities(kosProfileId));
        lightHouseBuilding.setMaxActionPoint(lightHouseBuildingConfig.getMaxActionPoint());
        return lightHouseBuilding;
    }

    @Override
    protected BaseBuildingConfig getBuildingConfig(Long level) {
        return buildingConfigDataSource.getConfig(BuildingName.LIGHTHOUSE, level);
    }
}

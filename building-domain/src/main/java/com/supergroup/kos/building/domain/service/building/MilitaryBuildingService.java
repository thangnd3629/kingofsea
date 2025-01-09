package com.supergroup.kos.building.domain.service.building;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.supergroup.core.constant.ErrorCode;
import com.supergroup.core.exception.KOSException;
import com.supergroup.kos.building.domain.command.GetMilitaryBuildingInfo;
import com.supergroup.kos.building.domain.constant.BuildingName;
import com.supergroup.kos.building.domain.model.building.MilitaryBuilding;
import com.supergroup.kos.building.domain.model.config.BaseBuildingConfig;
import com.supergroup.kos.building.domain.repository.persistence.building.BuildingConfigDataSource;
import com.supergroup.kos.building.domain.repository.persistence.building.MilitaryBuildingRepository;
import com.supergroup.kos.building.domain.service.profile.KosProfileService;
import com.supergroup.kos.building.domain.service.technology.TechnologyService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MilitaryBuildingService extends BaseBuildingService {

    private final MilitaryBuildingRepository militaryBuildingRepository;
    private final TechnologyService          technologyService;

    public MilitaryBuildingService(@Autowired KosProfileService kosProfileService,
                                   @Autowired BuildingConfigDataSource buildingConfigDataSource,
                                   @Autowired MilitaryBuildingRepository militaryBuildingRepository,
                                   @Autowired TechnologyService technologyService) {
        super(kosProfileService, buildingConfigDataSource);
        this.militaryBuildingRepository = militaryBuildingRepository;
        this.technologyService = technologyService;
    }

    public MilitaryBuilding getBuildingInfo(GetMilitaryBuildingInfo command) {
        var building = militaryBuildingRepository.findByKosProfileId(command.getKosProfileId())
                                                 .orElseThrow(() -> KOSException.of(ErrorCode.MILITARY_BUILDING_IS_NOT_FOUND));
        if (Objects.isNull(command.getCheckUnlockBuilding()) || command.getCheckUnlockBuilding()) {building.validUnlockBuilding(technologyService);}
        return building;
    }

    @Override
    protected BaseBuildingConfig getBuildingConfig(Long level) {
        return buildingConfigDataSource.getConfig(BuildingName.MILITARY, level);
    }
}

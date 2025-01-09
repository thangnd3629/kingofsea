package com.supergroup.kos.building.domain.service.building;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.supergroup.core.constant.ErrorCode;
import com.supergroup.core.exception.KOSException;
import com.supergroup.kos.building.domain.command.GetAllTechnologyCommand;
import com.supergroup.kos.building.domain.command.GetResearchBuildingInfo;
import com.supergroup.kos.building.domain.command.GetTechnologyCommand;
import com.supergroup.kos.building.domain.constant.BuildingName;
import com.supergroup.kos.building.domain.model.config.BaseBuildingConfig;
import com.supergroup.kos.building.domain.model.mining.ResearchBuilding;
import com.supergroup.kos.building.domain.model.technology.UserTechnology;
import com.supergroup.kos.building.domain.repository.persistence.building.BuildingConfigDataSource;
import com.supergroup.kos.building.domain.repository.persistence.building.ResearchBuildingRepository;
import com.supergroup.kos.building.domain.repository.persistence.technology.UserTechnologyRepository;
import com.supergroup.kos.building.domain.service.profile.KosProfileService;

import lombok.experimental.Delegate;

/**
 * @author ìdev
 * <p>
 * This class contains domain logic
 */
@Service
public class ResearchBuildingService extends BaseBuildingService {

    @Delegate
    private ResearchBuildingRepository researchBuildingRepository;
    private UserTechnologyRepository   userTechnologyRepository;
    private BuildingConfigDataSource   buildingConfigDataSource;

    public ResearchBuildingService(@Autowired ResearchBuildingRepository researchBuildingRepository,
                                   @Autowired UserTechnologyRepository userTechnologyRepository,
                                   @Autowired BuildingConfigDataSource buildingConfigDataSource,
                                   @Autowired KosProfileService kosProfileService) {
        super(kosProfileService, buildingConfigDataSource);
        this.researchBuildingRepository = researchBuildingRepository;
        this.userTechnologyRepository = userTechnologyRepository;
        this.buildingConfigDataSource = buildingConfigDataSource;
    }

    @Override
    protected BaseBuildingConfig getBuildingConfig(Long level) {
        return buildingConfigDataSource.getConfig(BuildingName.RESEARCH, level);
    }

    /**
     * Get building info (level, researched tech, ...)
     */
    public ResearchBuilding getBuildingInfo(GetResearchBuildingInfo command) {
        return researchBuildingRepository.findByKosProfileId(command.getKosProfileId())
                                         .orElseThrow(() -> KOSException.of(ErrorCode.RESEARCH_BUILDING_IS_NOT_FOUND));
    }

    public ResearchBuilding getByKosProfileId(Long kosProfileId) {
        return researchBuildingRepository.findByKosProfileId(kosProfileId)
                                         .orElseThrow(() -> KOSException.of(ErrorCode.RESEARCH_BUILDING_IS_NOT_FOUND));
    }

    /**
     * User's technology details (isResearched, effect, ...)
     */
    public UserTechnology getTechnologyDetail(GetTechnologyCommand command) {
        return userTechnologyRepository.findByKosProfileIdAndTechnologyCode(command.getKosProfileId(), command.getCode())
                                       .orElseThrow(() -> KOSException.of(ErrorCode.USER_TECHNOLOGY_NOT_FOUND));
    }

    /**
     * User's technology tree details
     */
    public List<UserTechnology> getAllTechnology(GetAllTechnologyCommand command) {
        return userTechnologyRepository.findByKosProfileIdAndTechnologyType(command.getKosProfileId(), command.getTechnologyType());
    }
}

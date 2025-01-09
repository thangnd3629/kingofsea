

package com.supergroup.kos.mapper;

import java.util.ArrayList;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.supergroup.kos.building.domain.constant.BuildingName;
import com.supergroup.kos.building.domain.model.config.CommunityBuildingConfig;
import com.supergroup.kos.dto.building.BuildingDTO;
import com.supergroup.kos.dto.building.UpgradeCommunityBuildingInfoResponse;

@Mapper
public interface UpgradeCommunityBuildingInfoMapper {
    @Mapping(target = "duration", source = "communityBuildingConfig.upgradeDuration")
    @Mapping(target = "reward.gloryPoint", source = "communityBuildingConfig.gpPointReward")
    @Mapping(target = "requirement.wood", source = "communityBuildingConfig.wood")
    @Mapping(target = "requirement.stone", source = "communityBuildingConfig.stone")
    @Mapping(target = "requirement.gold", source = "communityBuildingConfig.gold")
    @Mapping(target = "requirement.buildings", source = "communityBuildingConfig.castleLevelRequired", qualifiedByName = "getBuildingRequirement")
    @Mapping(target = "maxLevelListingRelic", source = "communityBuildingConfig.maxLevelListingRelic")
    UpgradeCommunityBuildingInfoResponse toDTO(CommunityBuildingConfig communityBuildingConfig);

    List<UpgradeCommunityBuildingInfoResponse> toDTOs(List<CommunityBuildingConfig> communityBuildingConfigs);
    @Named("getBuildingRequirement")
    default List<BuildingDTO> getBuildingRequirement(Long level) {
        if (level == null) {
            return null;
        }
        var buildingRequirements = new ArrayList<BuildingDTO>();
        buildingRequirements.add(new BuildingDTO().setName(BuildingName.CASTLE).setLevel(level));
        return buildingRequirements;
    }
}

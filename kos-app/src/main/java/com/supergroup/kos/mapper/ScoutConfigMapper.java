package com.supergroup.kos.mapper;

import java.util.ArrayList;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.supergroup.kos.building.domain.constant.BuildingName;
import com.supergroup.kos.building.domain.model.config.ScoutBuildingConfig;
import com.supergroup.kos.building.domain.model.config.ScoutCaseConfig;
import com.supergroup.kos.dto.building.BuildingDTO;
import com.supergroup.kos.dto.building.ScoutTrainingInfo;
import com.supergroup.kos.dto.building.ScoutUpgradeInfo;
import com.supergroup.kos.dto.scout.ScoutCaseConfigResponse;
import com.supergroup.kos.dto.scout.ScoutConfigDetail;

@Mapper
public interface ScoutConfigMapper {
    @Mapping(target = "duration", source = "upgradeDuration")
    @Mapping(target = "reward.gloryPoint", source = "gpPointReward")
    @Mapping(target = "requirement.wood", source = "wood")
    @Mapping(target = "requirement.stone", source = "stone")
    @Mapping(target = "requirement.gold", source = "gold")
    @Mapping(target = "requirement.buildings", source = "castleLevelRequired", qualifiedByName = "getBuildingRequirement")
    ScoutUpgradeInfo toUpgradeResponse(ScoutBuildingConfig scoutBuildingConfig);
    List<ScoutUpgradeInfo> toUpgradeResponses(List<ScoutBuildingConfig> scoutBuildingConfigs);

    ScoutConfigDetail toConfigDetail(ScoutBuildingConfig scoutBuildingConfig);
    List<ScoutConfigDetail> toConfigDetails(List<ScoutBuildingConfig> scoutBuildingConfigs);

    ScoutCaseConfigResponse toScoutCaseConfig(ScoutCaseConfig config);
    List<ScoutCaseConfigResponse> toScoutCaseConfigs(List<ScoutCaseConfig> configs);


    @Mapping(target = "wood", source = "costTrainingWood")
    @Mapping(target = "stone", source = "costTrainingStone")
    @Mapping(target = "gold", source = "costTrainingGold")
    ScoutTrainingInfo toScoutTrainingInfo(ScoutBuildingConfig config);
    List<ScoutTrainingInfo> toScoutTrainingInfoList(List<ScoutBuildingConfig> configs);

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

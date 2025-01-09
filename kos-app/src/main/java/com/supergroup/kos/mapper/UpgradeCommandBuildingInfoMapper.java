package com.supergroup.kos.mapper;

import java.util.ArrayList;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.supergroup.kos.building.domain.constant.BuildingName;
import com.supergroup.kos.building.domain.model.config.CommandBuildingConfig;
import com.supergroup.kos.dto.building.BuildingDTO;
import com.supergroup.kos.dto.building.UpgradeCommandBuildingInfoResponse;

@Mapper
public interface UpgradeCommandBuildingInfoMapper {

    @Mapping(target = "duration", source = "commandBuildingConfig.upgradeDuration")
    @Mapping(target = "reward.unLockMotherShipLevel", source = "commandBuildingConfig.unLockMotherShipLevel")
    @Mapping(target = "reward.unLockMotherShipQuality", source = "commandBuildingConfig.unLockMotherShipQuality")
    @Mapping(target = "reward.gloryPoint", source = "commandBuildingConfig.gpPointReward")
    @Mapping(target = "requirement.wood", source = "commandBuildingConfig.wood")
    @Mapping(target = "requirement.stone", source = "commandBuildingConfig.stone")
    @Mapping(target = "requirement.gold", source = "commandBuildingConfig.gold")
    @Mapping(target = "requirement.buildings", source = "commandBuildingConfig.researchLevelRequired",
             qualifiedByName = "researchBuildingRequirement")
    @Mapping(target = "slotMotherShip", source = "commandBuildingConfig.slotMotherShip")
    UpgradeCommandBuildingInfoResponse toDTO(CommandBuildingConfig commandBuildingConfig);

    List<UpgradeCommandBuildingInfoResponse> toDTOs(List<CommandBuildingConfig> commandBuildingConfigs);

    @Named("researchBuildingRequirement")
    default List<BuildingDTO> researchBuildingRequirement(Long level) {
        var buildingRequirements = new ArrayList<BuildingDTO>();
        buildingRequirements.add(new BuildingDTO().setLevel(level).setName(BuildingName.RESEARCH));
        return buildingRequirements;
    }
}

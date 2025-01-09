
package com.supergroup.kos.mapper;

import java.util.ArrayList;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.supergroup.kos.building.domain.constant.BuildingName;
import com.supergroup.kos.building.domain.model.config.WeaponSetLevelConfig;
import com.supergroup.kos.dto.building.BuildingDTO;
import com.supergroup.kos.dto.weapon.WeaponSetLevelConfigResponse;

@Mapper
public interface WeaponSetLevelConfigMapper {
    @Mapping(target = "quality", source = "weaponSetLevelConfig.level")
    @Mapping(target = "requirement.gold", source = "weaponSetLevelConfig.gold")
    @Mapping(target = "requirement.wood", source = "weaponSetLevelConfig.wood")
    @Mapping(target = "requirement.stone", source = "weaponSetLevelConfig.stone")
    @Mapping(target = "requirement.buildings", source = "weaponSetLevelConfig.armoryLevelRequired", qualifiedByName = "getBuildingRequirement")
    @Mapping(target = "percentStat", source = "weaponSetLevelConfig.percentStat")
    WeaponSetLevelConfigResponse toDTO(WeaponSetLevelConfig weaponSetLevelConfig);

    List<WeaponSetLevelConfigResponse> toDTOs(List<WeaponSetLevelConfig> weaponSetLevelConfigs);
    @Named("getBuildingRequirement")
    default List<BuildingDTO> getBuildingRequirement(Long level) {
        if (level == null) {
            return null;
        }
        var buildingRequirements = new ArrayList<BuildingDTO>();
        buildingRequirements.add(new BuildingDTO().setName(BuildingName.ARMORY).setLevel(level));
        return buildingRequirements;
    }
}

package com.supergroup.kos.mapper;

import java.util.Collection;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.supergroup.kos.building.domain.model.config.WeaponSetConfig;
import com.supergroup.kos.building.domain.model.weapon.WeaponSet;
import com.supergroup.kos.dto.weapon.WeaponSetConfigResponse;
import com.supergroup.kos.dto.weapon.WeaponSetResponse;

@Mapper
public interface WeaponSetMapper {

    @Mapping(target = "model.id", source = "weaponSet.weaponSetConfig.id")
    @Mapping(target = "model.name", source = "weaponSet.weaponSetConfig.name")
    @Mapping(target = "model.description", source = "weaponSet.weaponSetConfig.description")
    @Mapping(target = "model.thumbnail", source = "weaponSet.weaponSetConfig.thumbnail")
    @Mapping(target = "quality", source = "weaponSet.weaponSetLevelConfig.level")
    @Mapping(target = "motherShipId", source = "weaponSet.motherShip.id")
    @Mapping(target = "percentStat", source = "weaponSet.weaponSetLevelConfig.percentStat")
    @Mapping(target = "description", source = "weaponSet.weaponSetConfig.description")
    @Mapping(target = "model.stat", source = "weaponSet.weaponSetConfig.stat")
    @Mapping(target = "model.statType", source = "weaponSet.weaponSetConfig.stat_type")
    WeaponSetResponse toDTO(WeaponSet weaponSet);

    List<WeaponSetResponse> toDTOs(List<WeaponSet> weaponSets);

    Collection<WeaponSetConfigResponse> toDTOs(Collection<WeaponSetConfig> weaponSetConfigs);
}

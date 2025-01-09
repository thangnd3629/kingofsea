package com.supergroup.kos.mapper;

import java.util.Collection;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.supergroup.kos.building.domain.model.config.WeaponConfig;
import com.supergroup.kos.building.domain.model.weapon.Weapon;
import com.supergroup.kos.dto.weapon.WeaponConfigResponse;
import com.supergroup.kos.dto.weapon.WeaponResponse;

@Mapper
public interface WeaponMapper {
    @Mapping(target = "model.id", source = "weapon.weaponConfig.id")
    @Mapping(target = "model.name", source = "weapon.weaponConfig.name")
    @Mapping(target = "model.description", source = "weapon.weaponConfig.description")
    @Mapping(target = "model.thumbnail", source = "weapon.weaponConfig.thumbnail")
    @Mapping(target = "model.qualityExist", source = "weapon.qualityExist")
    @Mapping(target = "motherShipId", source = "weapon.motherShip.id")
    @Mapping(target = "model.stat", source = "weapon.weaponConfig.stat")
    @Mapping(target = "model.statType", source = "weapon.weaponConfig.stat_type")
    WeaponResponse toDTO(Weapon weapon);
    List<WeaponResponse> toDTOs(List<Weapon> weapon);

    Collection<WeaponConfigResponse> toDTOS(Collection<WeaponConfig> models);
}

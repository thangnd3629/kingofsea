package com.supergroup.kos.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.supergroup.kos.building.domain.model.weapon.Weapon;
import com.supergroup.kos.building.domain.model.weapon.WeaponSet;
import com.supergroup.kos.dto.weapon.WeaponSetMergeResponse;

@Mapper(uses = { WeaponSetMapper.class, WeaponSetConfigMapper.class, WeaponMapper.class, WeaponConfigMapper.class },
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface WeaponSetMergeMapper {

    @Mapping(target = "weaponSet.id", source = "weaponSet.id")
    @Mapping(target = "weaponSet.quality", source = "weaponSet.weaponSetLevelConfig.level")
    @Mapping(target = "weaponSet.percentStat", source = "weaponSet.weaponSetLevelConfig.percentStat")
    @Mapping(target = "weaponSet.model", source = "weaponSet.weaponSetConfig")
    WeaponSetMergeResponse toDTO(WeaponSet weaponSet);

    @Mapping(target = "weaponLost.id", source = "weapon.id")
    @Mapping(target = "weaponLost.model", source = "weapon.weaponConfig")
    WeaponSetMergeResponse toDTO(Weapon weapon);
}

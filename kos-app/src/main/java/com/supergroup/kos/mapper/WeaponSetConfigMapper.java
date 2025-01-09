package com.supergroup.kos.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import com.supergroup.kos.building.domain.model.config.WeaponSetConfig;
import com.supergroup.kos.dto.weapon.WeaponSetConfigResponse;

@Mapper
public interface WeaponSetConfigMapper {
    @Mappings({
            @Mapping(source = "stat_type", target = "statType")
    })
    WeaponSetConfigResponse toDTO(WeaponSetConfig weaponSetConfig);

    List<WeaponSetConfigResponse> toDTOs(List<WeaponSetConfig> weaponSetConfigs);


}

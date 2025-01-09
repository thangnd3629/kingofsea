package com.supergroup.kos.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import com.supergroup.kos.building.domain.model.config.WeaponConfig;
import com.supergroup.kos.dto.weapon.WeaponConfigResponse;

@Mapper
public interface WeaponConfigMapper {
    @Mappings({
            @Mapping(source = "stat_type", target = "statType")
    })
    WeaponConfigResponse toDTO(WeaponConfig weaponConfig);

    List<WeaponConfigResponse> toDTOs(List<WeaponConfig> weaponConfigList);

}

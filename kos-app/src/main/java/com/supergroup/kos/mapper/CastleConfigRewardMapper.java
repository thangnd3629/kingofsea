package com.supergroup.kos.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import com.supergroup.kos.building.domain.model.config.CastleConfig;
import com.supergroup.kos.dto.building.CastleConfigReward;

@Mapper
public interface CastleConfigRewardMapper {
    @Mappings({
            @Mapping(source = "gpPointReward", target = "gpGain")
    })
    CastleConfigReward toDTO(CastleConfig castleConfig);
    List<CastleConfigReward> toDTOs(List<CastleConfig> castleConfigList);
}

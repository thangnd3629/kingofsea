package com.supergroup.kos.mapper;

import org.mapstruct.Mapper;

import com.supergroup.kos.building.domain.model.config.CastleConfig;
import com.supergroup.kos.dto.building.CastleUpgradeInfoResponse;

@Mapper
public interface CastleUpgradeInfoMapper {
    CastleUpgradeInfoResponse toDTO(CastleConfig castleConfig);
}

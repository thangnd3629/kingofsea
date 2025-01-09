package com.supergroup.kos.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.supergroup.kos.building.domain.model.config.QueenBuildingConfig;
import com.supergroup.kos.dto.building.UpgradeQueenBuildingInfoResponse;

@Mapper
public interface UpgradeQueenBuildingInfoMapper {
    @Mapping(target = "duration", source = "queenBuildingConfig.upgradeDuration")
    @Mapping(target = "reward.gloryPoint", source = "queenBuildingConfig.gpPointReward")
    @Mapping(target = "reward.numberOfQueenCard", source = "queenBuildingConfig.queenCardReward")
    @Mapping(target = "requirement.wood", source = "queenBuildingConfig.wood")
    @Mapping(target = "requirement.stone", source = "queenBuildingConfig.stone")
    @Mapping(target = "requirement.gold", source = "queenBuildingConfig.gold")
    @Mapping(target = "maxQueen", source = "queenBuildingConfig.maxQueen")
    UpgradeQueenBuildingInfoResponse toDTO(QueenBuildingConfig queenBuildingConfig);

    List<UpgradeQueenBuildingInfoResponse> toDTOs(List<QueenBuildingConfig> queenBuildingConfigs);
}

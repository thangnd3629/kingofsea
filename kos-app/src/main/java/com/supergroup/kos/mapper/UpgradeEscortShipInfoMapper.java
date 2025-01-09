package com.supergroup.kos.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.supergroup.kos.building.domain.model.config.EscortShipLevelConfig;
import com.supergroup.kos.dto.ship.UpgradeEscortShipResponse;

@Mapper
public interface UpgradeEscortShipInfoMapper {
    @Mapping(target = "duration", source = "escortShipLevelConfig.upgradeDuration")
    @Mapping(target = "level", source = "escortShipLevelConfig.level")
    @Mapping(target = "requirement.wood", source = "escortShipLevelConfig.wood")
    @Mapping(target = "requirement.stone", source = "escortShipLevelConfig.stone")
    @Mapping(target = "requirement.gold", source = "escortShipLevelConfig.gold")
    @Mapping(target = "percentStat", source = "escortShipLevelConfig.percentStat")
    UpgradeEscortShipResponse toDTO(EscortShipLevelConfig escortShipLevelConfig);

    List<UpgradeEscortShipResponse> toDTOs(List<EscortShipLevelConfig> escortShipLevelConfigs);
}

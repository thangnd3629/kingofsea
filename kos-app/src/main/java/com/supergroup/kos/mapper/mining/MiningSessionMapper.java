package com.supergroup.kos.mapper.mining;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;

import com.supergroup.kos.building.domain.dto.seamap.MoveSessionDTO;
import com.supergroup.kos.building.domain.model.seamap.SeaMiningSession;
import com.supergroup.kos.building.domain.model.seamap.movesession.MoveSession;
import com.supergroup.kos.building.domain.model.seamap.reward.LoadedOnShipReward;
import com.supergroup.kos.dto.seamap.mining.MiningSessionResponse;
import com.supergroup.kos.mapper.seamap.activity.LineUpMapper;

@Mapper(uses = { LineUpMapper.class })
public interface MiningSessionMapper {
    @Mappings({
            @Mapping(source = "seaActivity.id", target = "activityId"),
            @Mapping(source = "seaActivity.kosProfile.id", target = "kosProfileId"),
            @Mapping(source = "seaActivity.lineUp", target = "lineUp")
    })
    MiningSessionResponse toResponse(SeaMiningSession model);
    @AfterMapping
    default void setLoadedOnShipReward(@MappingTarget MiningSessionResponse miningSessionResponse, SeaMiningSession session){
        LoadedOnShipReward loadedOnShipReward = session.getSeaActivity().getLoadedOnShipReward();
        miningSessionResponse.setLoadedOnShipReward((long) (loadedOnShipReward.getStone() + loadedOnShipReward.getGold() + loadedOnShipReward.getWood()));
    }
}

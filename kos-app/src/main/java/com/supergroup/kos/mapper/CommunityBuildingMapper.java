package com.supergroup.kos.mapper;

import java.util.Collection;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.supergroup.kos.building.domain.model.building.CommunityBuilding;
import com.supergroup.kos.building.domain.model.relic.Relic;
import com.supergroup.kos.dto.building.CommunityBuildingResponse;

@Mapper
public interface CommunityBuildingMapper {
    @Mapping(target = "numberOfRelic", source = "communityBuilding.ownRelics", qualifiedByName = "getNumberOfRelic")
    CommunityBuildingResponse toDTO(CommunityBuilding communityBuilding);

    List<CommunityBuildingResponse> toDTOs(List<CommunityBuilding> communityBuildings);

    @Named("getNumberOfRelic")
    default int getNumberOfRelic(Collection<Relic> ownRelics) {
        return ownRelics.size();
    }
}

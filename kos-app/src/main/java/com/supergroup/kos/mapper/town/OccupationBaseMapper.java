package com.supergroup.kos.mapper.town;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

import com.supergroup.asset.service.AssetService;
import com.supergroup.kos.building.domain.model.battle.OccupationBase;
import com.supergroup.kos.building.domain.model.seamap.SeaActivity;
import com.supergroup.kos.building.domain.service.seamap.activity.SeaActivityService;
import com.supergroup.kos.dto.town.OccupationBaseDTO;

@Mapper
public abstract class OccupationBaseMapper {

    @Autowired
    protected SeaActivityService seaActivityService;
    @Autowired
    protected AssetService       assetService;

    @Mapping(target = "avatarUrl", source = "avatarUrl", qualifiedByName = "toAvatarUrl")
    @Mapping(target = "activityIds", source = ".", qualifiedByName = "toActivityIds")
    public abstract OccupationBaseDTO toDTO(OccupationBase occupationBase, @Context Long kosProfileId);

    public abstract List<OccupationBaseDTO> toOccupationBaseDTOs(List<OccupationBase> occupationBases, @Context Long kosProfileId);

    @Named("toAvatarUrl")
    public String toAvatarUrl(String avatarUrl) {
        return Objects.nonNull(avatarUrl) ? assetService.getUrl(avatarUrl) : null;
    }

    @Named("toActivityIds")
    public List<Long> toActivityIds(OccupationBase occupationBase, @Context Long kosProfileId) {
        return seaActivityService.findByElementIdAndKosProfileId(occupationBase.getElementId(), kosProfileId).stream()
                                 .map(SeaActivity::getId)
                                 .collect(Collectors.toList());
    }
}

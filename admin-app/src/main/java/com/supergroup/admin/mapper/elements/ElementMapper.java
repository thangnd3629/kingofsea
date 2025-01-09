package com.supergroup.admin.mapper.elements;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;

import com.supergroup.admin.dto.elements.BossElementResponse;
import com.supergroup.admin.dto.elements.ElementResponse;
import com.supergroup.admin.dto.elements.ResourceElementResponse;
import com.supergroup.admin.dto.elements.ShipElementResponse;
import com.supergroup.admin.dto.elements.UserIslandResponse;
import com.supergroup.kos.building.domain.constant.seamap.ResourceIslandStatus;
import com.supergroup.kos.building.domain.constant.seamap.SeaElementType;
import com.supergroup.kos.building.domain.model.seamap.Anchor;
import com.supergroup.kos.building.domain.model.seamap.BossSea;
import com.supergroup.kos.building.domain.model.seamap.ResourceIsland;
import com.supergroup.kos.building.domain.model.seamap.SeaElement;
import com.supergroup.kos.building.domain.model.seamap.SeaMiningSession;
import com.supergroup.kos.building.domain.model.seamap.ShipElement;
import com.supergroup.kos.building.domain.model.seamap.UserBase;

@Mapper()
public interface ElementMapper {
    // user island
    @Mapping(target = "name", source = "islandName")
    @Mapping(target = "kosProfileId", source = "kosProfile.id")
    @Mapping(target = "level", source = "kosProfile.level")
    UserIslandResponse toUserIslandDTO(UserBase userIsland);

    // boss

    @Mappings({
            @Mapping(target = "status", source = "status"),
            @Mapping(target = "hpLost", source = "hpLost"),
    })
    BossElementResponse toBossDTO(BossSea bossSea);

    // resource island
    @Mappings({
            @Mapping(target = "status", source = "miningSession", qualifiedByName = "toResourceIslandStatus"),
            @Mapping(target = "kosProfileId", source = "miningSession", qualifiedByName = "toKosProfileMining"),
    })
    ResourceElementResponse toResourceDTO(ResourceIsland resourceIsland);

    @Mappings({
            @Mapping(target = "x", source = "coordinates.x"),
            @Mapping(target = "y", source = "coordinates.y"),
    })
    ResourceElementResponse toAnchorDTO(Anchor resourceIsland);

    @Mappings({
            @Mapping(source = "kosProfile.id", target = "kosProfileId"),
            @Mapping(source = "startTime", target = "timeStart")
    })
    ShipElementResponse toShipElementResponse(ShipElement shipElement);

    @Named("toResourceIslandStatus")
    default String toResourceStatus(SeaMiningSession seaMiningSession) {
        if (Objects.isNull(seaMiningSession)) {
            return ResourceIslandStatus.NORMAL.name();
        }
        return ResourceIslandStatus.MINING.name();
    }

    @Named("toKosProfileMining")
    default Long toKosProfileMining(SeaMiningSession seaMiningSession) {
        if (Objects.isNull(seaMiningSession)) {
            return null;
        }
        return seaMiningSession.getSeaActivity().getKosProfile().getId();
    }

    default ElementResponse map(SeaElement element) {
        if (element instanceof UserBase) {
            return this.toUserIslandDTO((UserBase) element)
                       .setType(SeaElementType.USER_BASE.name())
                       .setElementId(element.getSeaElementConfig().getId());
        } else if (element instanceof BossSea) {
            return this.toBossDTO((((BossSea) element)))
                       .setType(SeaElementType.BOSS.name())
                       .setElementId(element.getSeaElementConfig().getId());
        } else if (element instanceof ResourceIsland) {
            return this.toResourceDTO((ResourceIsland) element)
                       .setType(SeaElementType.RESOURCE.name())
                       .setElementId(element.getSeaElementConfig().getId());
        } else if (element instanceof Anchor) {
            return this.toAnchorDTO((Anchor) element);
        } else if (element instanceof ShipElement) {
            return this.toShipElementResponse((ShipElement) element)
                       .setType(SeaElementType.SHIP.name())
                       .setElementId(element.getSeaElementConfig().getId());
        } else {
            return null;
        }
    }

    default List<ElementResponse> maps(List<SeaElement> elements) {
        List<ElementResponse> list = new ArrayList<>();
        for (SeaElement s : elements) {
            list.add(this.map(s));
        }
        return list;
    }
}
package com.supergroup.kos.mapper.elements;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;

import com.supergroup.kos.building.domain.constant.seamap.ResourceIslandStatus;
import com.supergroup.kos.building.domain.constant.seamap.SeaElementType;
import com.supergroup.kos.building.domain.constant.seamap.UserBaseStatus;
import com.supergroup.kos.building.domain.model.seamap.Anchor;
import com.supergroup.kos.building.domain.model.seamap.BossSea;
import com.supergroup.kos.building.domain.model.seamap.ResourceIsland;
import com.supergroup.kos.building.domain.model.seamap.SeaElement;
import com.supergroup.kos.building.domain.model.seamap.SeaMiningSession;
import com.supergroup.kos.building.domain.model.seamap.ShipElement;
import com.supergroup.kos.building.domain.model.seamap.UserBase;
import com.supergroup.kos.dto.seamap.elements.BossElementResponse;
import com.supergroup.kos.dto.seamap.elements.ElementResponse;
import com.supergroup.kos.dto.seamap.elements.ResourceElementResponse;
import com.supergroup.kos.dto.seamap.elements.ShipElementResponse;
import com.supergroup.kos.dto.seamap.elements.UserIslandResponse;

@Mapper()
public interface ElementMapper {
    // user island
    @Mapping(target = "name", source = "islandName")
    @Mapping(target = "kosProfileId", source = "kosProfile.id")
    @Mapping(target = "level", source = "kosProfile.level")
    @Mapping(target = "battleId", source = "battle.id")
    @Mapping(target = "status", source = ".", qualifiedByName = "toUserBaseStatus")
    @Mapping(target = "invaderId", source = "invader.kosProfileInvader.id")
    UserIslandResponse toUserIslandDTO(UserBase userIsland);

    // boss

    @Mappings({
            @Mapping(target = "status", source = "status"),
            @Mapping(target = "hpLost", source = "hpLost"),
            @Mapping(target = "battleId", source = "battle.id"),
    })
    BossElementResponse toBossDTO(BossSea bossSea);

    // resource island
    @Mappings({
            @Mapping(target = "status", source = "miningSession", qualifiedByName = "toResourceIslandStatus"),
            @Mapping(target = "kosProfileId", source = "miningSession", qualifiedByName = "toKosProfileMining"),
            @Mapping(target = "tonnage", source = "miningSession.tonnage"),
            @Mapping(target = "timeStart", source = "miningSession.timeStart"),
            @Mapping(target = "collectedResource", source = "miningSession.collectedResource"),
            @Mapping(target = "battleId", source = "battle.id"),

    })
    ResourceElementResponse toResourceDTO(ResourceIsland resourceIsland);

    @Mappings({
            @Mapping(target = "x", source = "coordinates.x"),
            @Mapping(target = "y", source = "coordinates.y"),
            @Mapping(target = "battleId", source = "battle.id")
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

    @Named("toUserBaseStatus")
    default String toUserBaseStatus(UserBase userBase) {
        if (userBase.isOccupied()) {
            return UserBaseStatus.OCCUPIED.name();
        }
        return UserBaseStatus.NORMAL.name();
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

package com.supergroup.kos.building.domain.mapper;

import java.util.List;
import java.util.Objects;

import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.springframework.transaction.annotation.Transactional;

import com.supergroup.kos.building.domain.dto.profile.KosProfileCache;
import com.supergroup.kos.building.domain.dto.seamap.BossSeaCache;
import com.supergroup.kos.building.domain.dto.seamap.ResourceIslandCache;
import com.supergroup.kos.building.domain.dto.seamap.SeaActivityCache;
import com.supergroup.kos.building.domain.dto.seamap.SeaElementCache;
import com.supergroup.kos.building.domain.dto.seamap.SeaElementConfigCache;
import com.supergroup.kos.building.domain.dto.seamap.ShipElementCache;
import com.supergroup.kos.building.domain.dto.seamap.UserBaseCache;
import com.supergroup.kos.building.domain.model.config.seamap.SeaElementConfig;
import com.supergroup.kos.building.domain.model.profile.KosProfile;
import com.supergroup.kos.building.domain.model.seamap.BossSea;
import com.supergroup.kos.building.domain.model.seamap.Invader;
import com.supergroup.kos.building.domain.model.seamap.ResourceIsland;
import com.supergroup.kos.building.domain.model.seamap.SeaActivity;
import com.supergroup.kos.building.domain.model.seamap.SeaElement;
import com.supergroup.kos.building.domain.model.seamap.ShipElement;
import com.supergroup.kos.building.domain.model.seamap.UserBase;

@Mapper(uses = { KosProfileMapper.class })
public interface SeaElementMapper {

    @Named("seaElementToCacheCustom")
    default SeaElement map(SeaElementCache seaElementCache) {
        SeaElement seaElement = null;
        if (seaElementCache instanceof BossSeaCache) {
            seaElement = toModel((BossSeaCache) seaElementCache);
        } else if (seaElementCache instanceof ResourceIslandCache) {
            seaElement = toModel((ResourceIslandCache) seaElementCache);
        } else if (seaElementCache instanceof UserBaseCache) {
            seaElement = toModel((UserBaseCache) seaElementCache);
        } else if (seaElementCache instanceof ShipElementCache) {
            seaElement = toModel((ShipElementCache) seaElementCache);
        }
        if (Objects.nonNull(seaElement) && Objects.nonNull(seaElementCache.getInvader())) {
            seaElement.setInvader(new Invader().setKosProfileInvader(new KosProfile().setId(seaElementCache.getInvader().getId())));
        }
        return seaElement;
    }

    @Named("seaElementCacheToModelCustom")
    @Transactional
    default SeaElementCache map(SeaElement seaElement) {
        SeaElementCache cache = null;
        if (seaElement instanceof BossSea) {
            cache = toCache((BossSea) seaElement);
        } else if (seaElement instanceof ResourceIsland) {
            cache = toCache((ResourceIsland) seaElement);
        } else if (seaElement instanceof UserBase) {
            cache = toCache((UserBase) seaElement);
        } else if (seaElement instanceof ShipElement) {
            cache = toCache((ShipElement) seaElement);
        } else {
            cache = toCache(seaElement);
        }
        if (Objects.nonNull(cache) && Objects.nonNull(seaElement.getInvader())) {
            cache.setInvader(new KosProfileCache().setId(seaElement.getInvader().getKosProfileInvader().getId()));
        }
        return cache;
    }

    SeaElementCache toCache(SeaElement seaElement);

    @IterableMapping(qualifiedByName = "seaElementToCacheCustom")
    List<SeaElement> toModel(List<SeaElementCache> list);

    @IterableMapping(qualifiedByName = "seaElementCacheToModelCustom")
    List<SeaElementCache> toCache(List<SeaElement> list);

    BossSeaCache toCache(BossSea bossSea);

    BossSea toModel(BossSeaCache bossSea);

    ResourceIslandCache toCache(ResourceIsland resourceIsland);

    ResourceIsland toModel(ResourceIslandCache resourceIslandCache);

    @Mapping(source = "invader.kosProfileInvader", target = "invader")
    UserBaseCache toCache(UserBase userBase);

    @Mapping(source = "invader", target = "invader.kosProfileInvader")
    UserBase toModel(UserBaseCache userBaseCache);

    SeaActivity toModel(SeaActivityCache seaActivityCache);

    SeaActivityCache toCache(SeaActivity seaActivity);

    @Mappings({
            @Mapping(target = "type", expression = "java(seaElementConfig.getType())")
    })
    SeaElementConfigCache toCache(SeaElementConfig seaElementConfig);

    SeaElementConfig toModel(SeaElementConfigCache seaElementConfigCache);

    ShipElementCache toCache(ShipElement shipElement);

    ShipElement toModel(ShipElementCache shipElementCache);
}

package com.supergroup.kos.building.domain.mapper;

import org.mapstruct.Mapper;

import com.supergroup.kos.building.domain.dto.profile.KosProfileCache;
import com.supergroup.kos.building.domain.model.profile.KosProfile;

@Mapper
public interface KosProfileMapper {

    KosProfileCache toCache(KosProfile kosProfile);

    KosProfile toModel(KosProfileCache kosProfileCache);
}

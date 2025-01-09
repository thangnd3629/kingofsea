package com.supergroup.kos.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.supergroup.kos.building.domain.model.asset.Assets;
import com.supergroup.kos.dto.asset.KosAssetsResponse;

@Mapper
public interface KosAssetsMapper {
    @Mapping(target = "people", source = "totalPeople")
    KosAssetsResponse toDTO(Assets assets);
}

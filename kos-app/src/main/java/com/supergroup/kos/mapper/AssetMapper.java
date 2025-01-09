package com.supergroup.kos.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import com.supergroup.kos.building.domain.model.asset.Assets;
import com.supergroup.kos.dto.asset.AssetResponse;

@Mapper
public interface AssetMapper {
    @Mappings({
            @Mapping(source = "totalPeople", target = "people")
    })
    AssetResponse toDTO(Assets assets);
}

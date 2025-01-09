package com.supergroup.admin.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import com.supergroup.admin.dto.AssetResponse;
import com.supergroup.kos.building.domain.model.asset.Assets;

@Mapper
public interface AssetMapper {
    @Mappings({
            @Mapping(source = "totalPeople", target = "people")
    })
    AssetResponse toDTO(Assets assets);
}

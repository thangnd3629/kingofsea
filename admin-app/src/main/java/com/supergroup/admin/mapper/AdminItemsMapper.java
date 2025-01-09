package com.supergroup.admin.mapper;

import org.mapstruct.Mapper;

import com.supergroup.admin.dto.ItemResponse;
import com.supergroup.kos.building.domain.model.item.Item;

@Mapper
public interface AdminItemsMapper {
    ItemResponse toResponse(Item item);
}

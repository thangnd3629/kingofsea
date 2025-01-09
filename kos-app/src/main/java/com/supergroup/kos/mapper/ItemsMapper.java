package com.supergroup.kos.mapper;

import java.util.Collection;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import com.supergroup.kos.building.domain.dto.item.DetailUserItem;
import com.supergroup.kos.building.domain.model.item.Item;
import com.supergroup.kos.building.domain.model.item.UserItem;
import com.supergroup.kos.dto.item.DetailUserItemResponse;
import com.supergroup.kos.dto.item.ItemConfigResponse;
import com.supergroup.kos.dto.item.ItemsResponse;

@Mapper
public interface ItemsMapper {

    ItemsResponse toResponse(DetailUserItem item);

    @Mappings({
            @Mapping(source = "item.id", target = "id"),
            @Mapping(source = "item.namespace", target = "namespace"),
            @Mapping(source = "item.type", target = "type"),
            @Mapping(source = "item.thumbnail", target = "thumbnail"),
            @Mapping(source = "item.description", target = "description"),
            @Mapping(source = "item.expiry", target = "expiry")
    })
    DetailUserItemResponse toResponse(UserItem userItem);

    ItemConfigResponse toConfigDTO(Item item);
    Collection<ItemConfigResponse> toConfigDTOS(Collection<Item> items);
}

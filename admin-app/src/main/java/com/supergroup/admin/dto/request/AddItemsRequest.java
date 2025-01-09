package com.supergroup.admin.dto.request;

import com.supergroup.kos.building.domain.constant.item.ItemId;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class AddItemsRequest {
    private ItemId itemId;
    private Long   kosProfileId;
}

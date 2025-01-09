package com.supergroup.admin.dto;

import com.supergroup.kos.building.domain.constant.item.ItemId;
import com.supergroup.kos.building.domain.constant.item.ItemType;
import com.supergroup.kos.building.domain.constant.item.NameSpaceKey;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class ItemResponse {
    private ItemId                 id;
    private NameSpaceKey           namespace;
    private ItemType               type;
    private String                 name;
    private String                 thumbnail;
    private String                 description;
    private Long                   expiry;
    private String                 unit;
}

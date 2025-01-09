package com.supergroup.kos.dto.item;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class ItemsResponse {
    private String id;
    private Long   amount;
    private String type;
    private String name;
    private String thumbnail;
    private String description;
    private Long   expiry;
    private String unit;
}

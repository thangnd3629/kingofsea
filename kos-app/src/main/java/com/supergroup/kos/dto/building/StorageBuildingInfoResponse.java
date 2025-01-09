package com.supergroup.kos.dto.building;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(chain = true)
public class StorageBuildingInfoResponse {
    private Long    lootableCap;
    private Long    level;
    private String  description;
    private Storage storage;
    private Long    protectionCap;
}
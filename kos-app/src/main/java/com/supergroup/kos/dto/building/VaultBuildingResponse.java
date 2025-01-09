package com.supergroup.kos.dto.building;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class VaultBuildingResponse {
    private Long   level;
    private Double protectPercent;
    private String description;
}

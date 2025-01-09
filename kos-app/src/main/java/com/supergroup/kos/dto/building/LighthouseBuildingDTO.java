package com.supergroup.kos.dto.building;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class LighthouseBuildingDTO {
    private Long   level;
    private String description;
    private Long   actionPoint;
    private Long   actionPointUsed;
}

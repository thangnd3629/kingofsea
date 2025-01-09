package com.supergroup.kos.building.domain.dto.seamap;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class CoordinatesDTO {
    private Long x;
    private Long y;
}

package com.supergroup.kos.building.domain.dto.seamap;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class OccupiedAreaDTO {
    private Long width; // Ox
    private Long height; // Oy
    private Long length; // Oz
}

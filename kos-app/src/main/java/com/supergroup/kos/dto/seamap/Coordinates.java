package com.supergroup.kos.dto.seamap;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class Coordinates {
    private Double x;
    private Double y;
}

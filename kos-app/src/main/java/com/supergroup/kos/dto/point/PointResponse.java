package com.supergroup.kos.dto.point;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class PointResponse {
    private Long gp;
    private Long tp;
    private Long mp;
    private Double mpMultiplier;
}

package com.supergroup.kos.dto.building;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class RequiredUpgradeBase {
    private Long wood;
    private Long stone;
    private Long gold;
    private Long time; // second
}

package com.supergroup.kos.dto.item;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(chain = true)
public class EffectItem {
    private String unit;
    private String name;
    private Long   value;
}
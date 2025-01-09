package com.supergroup.kos.building.domain.model.scout;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class Location {
    private Long x;
    private Long y;
}

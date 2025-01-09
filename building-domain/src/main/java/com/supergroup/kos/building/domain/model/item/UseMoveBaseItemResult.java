package com.supergroup.kos.building.domain.model.item;

import com.supergroup.kos.building.domain.model.seamap.Coordinates;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class UseMoveBaseItemResult extends UseItemResult {
    private Coordinates newLocation;
}

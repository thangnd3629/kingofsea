package com.supergroup.kos.dto.item;

import com.supergroup.kos.building.domain.dto.seamap.CoordinatesDTO;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class UseMoveBaseItemResponse extends UseItemResponse {
    private CoordinatesDTO newLocation;
}

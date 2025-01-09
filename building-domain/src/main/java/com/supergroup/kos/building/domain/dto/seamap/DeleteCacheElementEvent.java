package com.supergroup.kos.building.domain.dto.seamap;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class DeleteCacheElementEvent implements Serializable {
    private Long seaElementId;
}

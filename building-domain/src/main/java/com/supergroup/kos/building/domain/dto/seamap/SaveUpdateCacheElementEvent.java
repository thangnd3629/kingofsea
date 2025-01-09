package com.supergroup.kos.building.domain.dto.seamap;

import java.io.Serializable;

import com.supergroup.kos.building.domain.model.seamap.SeaElement;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SaveUpdateCacheElementEvent implements Serializable {
    private SeaElement seaElement;
}

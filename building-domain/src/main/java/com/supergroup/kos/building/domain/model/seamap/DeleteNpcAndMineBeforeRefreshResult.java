package com.supergroup.kos.building.domain.model.seamap;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class DeleteNpcAndMineBeforeRefreshResult {
    private Integer              totalDelete;
    private List<ElementRefresh> elementNotDelete;
}

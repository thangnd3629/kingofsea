package com.supergroup.kos.building.domain.model.mining;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class DataMiningResource {
    private Long   worker;
    private Double speedPerWorker;
    private Long   currentResources;
    private Long   capacity;
}

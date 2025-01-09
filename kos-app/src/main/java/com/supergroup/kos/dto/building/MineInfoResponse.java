package com.supergroup.kos.dto.building;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class MineInfoResponse {
    private Long   level;
    private String description;
    private Long   storage;
    private Long   numWorker;
    private Double speedWorker;
    private Long   maxWorker;
}
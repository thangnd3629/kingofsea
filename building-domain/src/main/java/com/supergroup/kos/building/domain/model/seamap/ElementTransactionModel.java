package com.supergroup.kos.building.domain.model.seamap;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class ElementTransactionModel {
    private Long   id;
    private Long   kosProfileId;
    private String name;
    private Long   elementId;
    private String type;
    private Long   x;
    private Long   y;
    private Long   dependentElementId;
}

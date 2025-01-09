package com.supergroup.admin.dto.elements;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class ElementResponse {
    private Long   id;
    private Long   elementId;
    private String type;
    private Long   x;
    private Long   y;
    private Long   dependentElementId;
}

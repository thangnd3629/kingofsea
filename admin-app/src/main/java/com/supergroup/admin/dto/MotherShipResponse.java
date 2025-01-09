package com.supergroup.admin.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class MotherShipResponse {
    private Long                            id;
    private String name;
}

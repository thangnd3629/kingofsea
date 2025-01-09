package com.supergroup.admin.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class ShipLoad {
    private Double gold;
    private Double stone;
    private Double wood;
}

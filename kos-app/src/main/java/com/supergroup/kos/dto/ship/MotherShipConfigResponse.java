package com.supergroup.kos.dto.ship;

import com.supergroup.kos.building.domain.constant.MotherShipTypeKey;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class MotherShipConfigResponse {
    private Long              id;
    private String            name;
    private String            thumbnail;
    private String            description;
    private MotherShipTypeKey type;
    private Long              atk1;
    private Long              def1;
    private Long              tng;
    private Long              hp;
    private Long              cmd;
    private Long              speed;
    private Long              dodge;
    private Long              gold;
    private Double            recoverySpeed;
}

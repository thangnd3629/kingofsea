package com.supergroup.kos.building.domain.model.config;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class InitAssetKosConfig {
    private  Long   mp;
    private  Long   gp;
    private  Long   tp;
    private  Double gold;
    private  Double stone;
    private  Double wood;
    private  Double people;
    private  Long   castleLevel;
}

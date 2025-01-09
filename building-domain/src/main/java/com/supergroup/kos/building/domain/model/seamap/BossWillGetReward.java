package com.supergroup.kos.building.domain.model.seamap;

import java.io.Serializable;

import javax.persistence.Embeddable;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@Embeddable
public class BossWillGetReward implements Serializable {
    private Long wood;
    private Long stone;
    private Long gold;
    private Long gp;
}

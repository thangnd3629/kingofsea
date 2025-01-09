package com.supergroup.kos.building.domain.model.seamap;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class BossMayGetRewardItem implements Serializable {
    private Integer      amount;
    private Double       rate;
    private List<String> items;
}
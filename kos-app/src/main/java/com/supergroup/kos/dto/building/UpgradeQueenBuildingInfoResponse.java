package com.supergroup.kos.dto.building;

import com.supergroup.kos.dto.upgrade.UpgradeRequirement;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class UpgradeQueenBuildingInfoResponse {
    private Long               duration;
    private QueenUpgradeReward reward;
    private Long               level;
    private UpgradeRequirement requirement;
    private Double             protectPercent;
    private Long               maxQueen;

}

package com.supergroup.kos.dto.upgrade;

import java.util.List;

import com.supergroup.kos.dto.RequirementDTO;
import com.supergroup.kos.dto.technology.TechnologyTreeItem;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class UpgradeResearchBuildingInfoResponse {
    private Long          duration;
    private UpgradeReward            reward;
    private List<TechnologyTreeItem> unlock;
    private Long                     level;
    private Double         convertRate;
    private RequirementDTO requirement;
}
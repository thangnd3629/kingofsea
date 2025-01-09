package com.supergroup.kos.building.domain.command;

import com.supergroup.kos.building.domain.constant.MiningType;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class GetDataMiningCommand {
    private Long       useId;
    private MiningType miningType;
}

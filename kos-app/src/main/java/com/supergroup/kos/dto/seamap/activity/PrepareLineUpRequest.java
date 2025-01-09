package com.supergroup.kos.dto.seamap.activity;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.supergroup.kos.building.domain.command.PrepareShipLineupCommand;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PrepareLineUpRequest {
    @NotNull
    private Long                     motherShipId;
    @Valid
    @NotNull
    private PrepareShipLineupCommand lineUp;
}

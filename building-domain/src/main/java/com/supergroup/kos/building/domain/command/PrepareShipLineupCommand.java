package com.supergroup.kos.building.domain.command;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.supergroup.kos.building.domain.dto.seamap.EscortSquadDTO;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class PrepareShipLineupCommand {
    private Long motherShipId;
    private Long kosProfileId;
    @NotNull
    @Valid
    private List<EscortSquadDTO> escortShips;
}

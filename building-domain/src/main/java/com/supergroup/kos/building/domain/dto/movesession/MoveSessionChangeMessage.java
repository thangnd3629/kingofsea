package com.supergroup.kos.building.domain.dto.movesession;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class MoveSessionChangeMessage {
    private Long sourceId;
    private Long destinationId;
    private Long seaActivityId;
}

package com.supergroup.kos.building.domain.dto.seamap;

import java.io.Serializable;

import com.supergroup.kos.building.domain.model.seamap.movesession.MoveSession;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CreateMoveSessionEvent implements Serializable {
    private MoveSession moveSession;
}

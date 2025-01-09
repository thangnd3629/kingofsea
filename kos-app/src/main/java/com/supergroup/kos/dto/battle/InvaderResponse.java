package com.supergroup.kos.dto.battle;

import java.util.List;

import com.supergroup.kos.building.domain.dto.seamap.CoordinatesDTO;
import com.supergroup.kos.dto.seamap.activity.ShipLineUpDTO;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors
public class InvaderResponse {
    private String              name;
    private Long                level;
    private CoordinatesDTO      coordinates;
    private String              avatarUrl;
    private List<ShipLineUpDTO> shipLineUps;
}

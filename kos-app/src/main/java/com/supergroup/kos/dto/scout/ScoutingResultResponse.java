package com.supergroup.kos.dto.scout;

import java.util.List;

import com.supergroup.kos.building.domain.model.scout.Location;
import com.supergroup.kos.building.domain.model.ship.EscortShipScoutingResult;
import com.supergroup.kos.building.domain.model.ship.MotherShipScoutingResult;
import com.supergroup.kos.dto.queen.QueenConfigResponse;
import com.supergroup.kos.dto.relic.RelicConfigResponse;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class ScoutingResultResponse {
    private String                         username;
    private String                         avatarUrl;
    private Location                       location;
    private Long                           soliderDie;
    private Boolean                        inOnline;
    private Long                           lastActiveFrom; //seconds
    private Long                           wood;
    private Long                           stone;
    private Long                           gold;
    private List<QueenConfigResponse>      queens;
    private List<RelicConfigResponse>      relics;
    private List<EscortShipScoutingResult> escortShips;
    private List<MotherShipScoutingResult> motherShips;
}

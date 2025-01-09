package com.supergroup.kos.building.domain.model.scout;

import java.util.List;

import com.supergroup.kos.building.domain.model.ship.EscortShipScoutingResult;
import com.supergroup.kos.building.domain.model.ship.MotherShipScoutingResult;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class ScoutingResult {
    private String                         username;
    private Long                           x;
    private Long                           y;
    private String                         avatarUrl;
    private Long                           soliderDie;
    private Boolean                        inOnline;
    private Long                           lastActiveFrom; // seconds
    private Long                           wood;
    private Long                           stone;
    private Long                           gold;
    private List<Long>                     queens;
    private List<Long>                     relics;
    private List<EscortShipScoutingResult> escortShips;
    private List<MotherShipScoutingResult> motherShips;
}

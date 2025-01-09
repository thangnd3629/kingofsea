package com.supergroup.kos.building.domain.command;

import com.supergroup.kos.building.domain.constant.MissionType;
import com.supergroup.kos.building.domain.model.profile.KosProfile;
import com.supergroup.kos.building.domain.model.seamap.Coordinates;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class ActivityScoutCommand {
    private KosProfile kosProfile;
    private Long        numberArmy;
    private MissionType type;
    private Coordinates coordinates;
}

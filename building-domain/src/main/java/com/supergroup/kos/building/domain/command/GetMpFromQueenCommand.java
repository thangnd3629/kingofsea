package com.supergroup.kos.building.domain.command;

import com.supergroup.kos.building.domain.model.profile.KosProfile;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class GetMpFromQueenCommand {
    private KosProfile kosProfile;
    private boolean    isIgnoreCheckOccupy;
}

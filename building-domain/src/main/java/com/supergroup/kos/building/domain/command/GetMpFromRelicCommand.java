package com.supergroup.kos.building.domain.command;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class GetMpFromRelicCommand {
    private Long    kosProfileId;
    private boolean isIgnoreCheckOccupy;
}

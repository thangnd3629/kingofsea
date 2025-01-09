package com.supergroup.kos.building.domain.command;

import com.supergroup.kos.building.domain.model.profile.KosProfile;
import com.supergroup.kos.building.domain.model.seamap.Coordinates;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class GetElementsByAreaCommand {
    private final Coordinates coordinates; // coordinate of center point
    private final Long        height; // height view port
    private final Long        width; // width view port
    private final KosProfile  kosProfile; // use to filter scout ship
}

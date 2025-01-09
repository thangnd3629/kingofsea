package com.supergroup.kos.building.domain.command;

import com.supergroup.kos.building.domain.model.seamap.Coordinates;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class GetElementByCoordinatesCommand {
    private final Coordinates coordinates;
}

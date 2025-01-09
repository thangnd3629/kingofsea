package com.supergroup.kos.building.domain.service.seamap.item;

import com.supergroup.kos.building.domain.model.seamap.Coordinates;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class ApplyMoveBaseSpecifiedCommand extends ApplyItemCommand {
    private final Coordinates newLocation;
}

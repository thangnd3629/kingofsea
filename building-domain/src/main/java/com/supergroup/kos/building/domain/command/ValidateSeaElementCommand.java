package com.supergroup.kos.building.domain.command;

import com.supergroup.kos.building.domain.model.seamap.SeaElement;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class ValidateSeaElementCommand {
    private final SeaElement element;
}


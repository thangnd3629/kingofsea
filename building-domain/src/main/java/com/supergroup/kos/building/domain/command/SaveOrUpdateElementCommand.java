package com.supergroup.kos.building.domain.command;

import com.supergroup.kos.building.domain.model.seamap.SeaElement;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SaveOrUpdateElementCommand {
    private final SeaElement element;
}

package com.supergroup.kos.building.domain.command;

import com.supergroup.kos.building.domain.constant.StorageType;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@RequiredArgsConstructor
@Accessors(chain = true)
public class GetStorageBuildingCommand {
    private final StorageType type;
    private final Long        kosProfileId;
    private       Boolean     checkValidUnlock;
}

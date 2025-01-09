package com.supergroup.kos.building.domain.command;

import com.supergroup.kos.building.domain.constant.EscortShipType;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class QueueEscortShipCommand {
    private final Long           kosProfileId;
    private final EscortShipType type;
    private final Long           amount;
    private final Boolean        isQueueing;

}
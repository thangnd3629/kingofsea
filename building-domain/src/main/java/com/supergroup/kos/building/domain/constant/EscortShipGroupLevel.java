package com.supergroup.kos.building.domain.constant;

import java.util.Arrays;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EscortShipGroupLevel {
    WOOD(0),
    STEEL(1),
    BRASS(2),
    SILVER(3),
    GOLD(4),
    DIAMOND(5),
    TITAN(6);
    private final Integer key;

    public static EscortShipGroupLevel of(Integer key) {
        return Arrays.stream(EscortShipGroupLevel.values()).filter(escortShipGroupLevel -> escortShipGroupLevel.key.equals(key))
                     .findFirst().orElseThrow(IllegalArgumentException::new);
    }

    public static EscortShipGroupLevel getNextLevel(EscortShipGroupLevel escortShipGroupLevel) {
        return EscortShipGroupLevel.of(escortShipGroupLevel.getKey() + 1);
    }
}

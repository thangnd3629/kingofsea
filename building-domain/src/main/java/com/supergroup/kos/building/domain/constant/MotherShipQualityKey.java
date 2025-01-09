package com.supergroup.kos.building.domain.constant;

import java.util.Arrays;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MotherShipQualityKey {
    COMMON(0),
    RARE(1),
    EPIC(2),
    LEGENDARY(3);
    private final Integer key;

    public static MotherShipQualityKey of(Integer key) {
        return Arrays.stream(MotherShipQualityKey.values()).filter(escortShipGroupLevel -> escortShipGroupLevel.key.equals(key))
                     .findFirst().orElseThrow(IllegalArgumentException::new);
    }

    public static MotherShipQualityKey getNextQuality(MotherShipQualityKey motherShipQualityKey) {
        return MotherShipQualityKey.of(motherShipQualityKey.getKey() + 1);
    }
}
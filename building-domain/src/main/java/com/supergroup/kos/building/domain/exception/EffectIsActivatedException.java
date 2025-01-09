package com.supergroup.kos.building.domain.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EffectIsActivatedException extends RuntimeException {
    /**
     * do not create this class by constructor, using static method {@code of} instead
     */

    private String code;

    public EffectIsActivatedException(String effectName) {
        super(effectName + "is activate");
        this.code = effectName.toUpperCase().replace(" ", "_") + "IS_ACTIVATED";
    }
}

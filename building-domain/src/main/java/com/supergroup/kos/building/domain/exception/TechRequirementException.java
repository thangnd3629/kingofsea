package com.supergroup.kos.building.domain.exception;

import com.supergroup.core.constant.ErrorCode;
import com.supergroup.core.exception.KOSException;
import com.supergroup.kos.building.domain.model.technology.Technology;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TechRequirementException extends KOSException {

    private Technology technology;

    /**
     * do not create this class by constructor, using static method {@code of} instead
     */
    public TechRequirementException(ErrorCode errorCode, Technology technology) {
        super(errorCode);
        this.technology = technology;
    }

}

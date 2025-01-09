package com.supergroup.core.exception;

import com.supergroup.core.constant.ErrorCode;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data(staticConstructor = "of")
public class KOSException extends RuntimeException {
    protected final ErrorCode code;

    // do not create this class by constructor, using static method instead
    private KOSException(String message) {
        super(message);
        this.code = ErrorCode.SERVER_ERROR; // by default
    }

    /**
     * do not create this class by constructor, using static method {@code of} instead
     */
    public KOSException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode;
    }
}

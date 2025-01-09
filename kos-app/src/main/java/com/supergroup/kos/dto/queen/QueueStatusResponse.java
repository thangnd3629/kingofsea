package com.supergroup.kos.dto.queen;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class QueueStatusResponse {
    private Long amount;
    private Long duration; // millis
    private Long current; // millis
}

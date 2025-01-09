package com.supergroup.kos.dto.queen;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class QueenResponse {
    private Long                id;
    private QueenConfigResponse model;
}


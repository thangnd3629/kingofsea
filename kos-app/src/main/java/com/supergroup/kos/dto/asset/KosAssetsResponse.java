package com.supergroup.kos.dto.asset;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class KosAssetsResponse {
    private Long gold;
    private Long wood;
    private Long stone;
    private Long people; // max people
    private Long idlePeople; // max people
}

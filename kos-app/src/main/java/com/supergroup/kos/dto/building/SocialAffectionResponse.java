package com.supergroup.kos.dto.building;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class SocialAffectionResponse {
    private Long   totalMp;
    private Long   mpFromRelic;
    private Long   mpFromQueens;
    private Long   mpFromCastle;
    private Double peopleProduction;
}

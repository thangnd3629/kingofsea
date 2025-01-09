package com.supergroup.kos.dto.building;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class CommunityBuildingResponse {
    private Long level;
    private Long mpGained;
    private Long maxListingRelic;
    private Long maxLevelListingRelic;
    private Long numberOfRelic;
}

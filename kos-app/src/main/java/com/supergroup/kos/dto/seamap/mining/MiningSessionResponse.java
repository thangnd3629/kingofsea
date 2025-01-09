package com.supergroup.kos.dto.seamap.mining;

import com.supergroup.kos.dto.seamap.activity.ShipLineUpDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MiningSessionResponse {
    private Long          id;
    private Long          activityId;
    private Long          loadedOnShipReward;
    private Long          kosProfileId;
    private Long          collectedResource;
    private ShipLineUpDTO lineUp;
}

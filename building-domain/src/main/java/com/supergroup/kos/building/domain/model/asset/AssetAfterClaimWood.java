package com.supergroup.kos.building.domain.model.asset;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class AssetAfterClaimWood extends AssetsAfterClaim {
    private Long wood;
}

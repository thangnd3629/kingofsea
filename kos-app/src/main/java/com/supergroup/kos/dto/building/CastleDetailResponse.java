package com.supergroup.kos.dto.building;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class CastleDetailResponse {
    private Long                     currentLevel;
    private List<CastleConfigReward> data;
}

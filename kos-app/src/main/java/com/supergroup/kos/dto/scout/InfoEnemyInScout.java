package com.supergroup.kos.dto.scout;

import com.supergroup.kos.building.domain.model.scout.Location;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class InfoEnemyInScout {
    private Long     level;
    private String   name;
    private Location location;
    private String   avatarUrl;
    private Long     enemy;
    private Double   speed;
}

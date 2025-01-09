package com.supergroup.kos.building.domain.dto.seamap;

import com.supergroup.kos.building.domain.dto.profile.KosProfileCache;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class UserBaseCache extends SeaElementCache {
    private String          islandName;
    private KosProfileCache kosProfile;
}

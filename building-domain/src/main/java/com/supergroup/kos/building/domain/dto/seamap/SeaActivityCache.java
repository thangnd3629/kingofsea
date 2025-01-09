package com.supergroup.kos.building.domain.dto.seamap;

import org.springframework.data.redis.core.RedisHash;

import com.supergroup.kos.building.domain.dto.profile.KosProfileCache;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@RedisHash("SeaActivity")
public class SeaActivityCache {
    private Long            id;
    private KosProfileCache kosProfile;
    private Long shipLineUp = 1L; //todo

}

package com.supergroup.kos.building.domain.dto.seamap;

import java.time.LocalDateTime;

import org.springframework.data.redis.core.RedisHash;

import com.supergroup.kos.building.domain.constant.seamap.ResourceIslandType;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@RedisHash("MiningResourceSeaSession")
public class MiningResourceSeaSessionCache {
    private Long               id;
    private LocalDateTime      timeStart;
    private SeaActivityCache   seaActivity;
    private Double             duration;
    private Double             speed;
    private Double             collectedResource;
    private ResourceIslandType resourceType;
    private Double             tonnage; // remaining ship cap
}

package com.supergroup.kos.building.domain.dto.seamap;

import org.springframework.data.redis.core.RedisHash;

import lombok.Getter;
import lombok.Setter;

@RedisHash("BattleCache")
@Getter
@Setter
public class BattleCache {
    private Long id;
}

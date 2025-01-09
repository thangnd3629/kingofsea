package com.supergroup.kos.building.domain.dto.profile;

import org.springframework.data.redis.core.RedisHash;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@RedisHash("KosProfile")
@Accessors(chain = true)
public class KosProfileCache {
    private Long id;
    private Long level;
}

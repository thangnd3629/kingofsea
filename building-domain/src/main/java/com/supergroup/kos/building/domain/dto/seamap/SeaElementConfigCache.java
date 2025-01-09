package com.supergroup.kos.building.domain.dto.seamap;

import javax.persistence.Id;

import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import com.supergroup.kos.building.domain.constant.seamap.SeaElementType;

import lombok.Getter;
import lombok.Setter;

@RedisHash("ElementConfig")
@Getter
@Setter
public class SeaElementConfigCache {
    @Id
    private Long id;

    private Long            level;
    private String          name;
    private String          thumbnail;
    private OccupiedAreaDTO occupied;
    @Indexed
    private SeaElementType  type;
}

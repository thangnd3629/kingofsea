package com.supergroup.kos.building.domain.dto.seamap;

import java.util.List;

import javax.persistence.Id;

import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import com.supergroup.kos.building.domain.dto.profile.KosProfileCache;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@RedisHash("SeaElement")
public class SeaElementCache {
    @Id
    private Long id;

    private Boolean isRefreshable = false;
    /**
     * We will separate map into same size piece
     * vd: This is EXAMPLE, NOT right logic
     * ==================================
     * ||       ||      ||      ||      ||
     * ||  1,1  ||  1,2 ||  1,3 ||  1,4 ||
     * ==================================
     * ||       ||      ||      ||      ||
     * ||  2,1  ||  2,2 ||  2,3 ||  x,y ||
     * ==================================
     * This field must not be null. This is index key in redis database
     * Ox name for parcel
     */
    @Indexed
    private Integer parcelX;

    /**
     * This field must not be null. This is index key in redis database
     * Ox name for parcel
     */
    @Indexed
    private Integer parcelY;

    // This field must not be null. This is index key in redis database
    @Indexed
    private Long x;
    // This field must not be null. This is index key in redis database
    @Indexed
    private Long y;

    private Long dependentElementId;

    /**
     * flag active island
     */
    @Indexed
    private Boolean active = false;

    private List<SeaActivityCache> activityCaches;
    private SeaElementConfigCache  seaElementConfig;
    private BattleCache            battle;
    private KosProfileCache        invader;
}

package com.supergroup.kos.building.domain.repository.cache.seamap;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.supergroup.core.repository.BaseRedisRepository;
import com.supergroup.kos.building.domain.constant.seamap.SeaElementType;
import com.supergroup.kos.building.domain.dto.seamap.SeaElementCache;

@Repository("SeaMapElementCacheRepository")
public interface SeaElementCacheRepository extends BaseRedisRepository<SeaElementCache, Long> {
    List<SeaElementCache> findByParcelXAndParcelYAndActive(Integer x, Integer y, Boolean isActive);

    List<SeaElementCache> findBySeaElementConfig_Type(SeaElementType type);

    default void deleteBySeaElementConfigType(SeaElementType type) {
        var list = findBySeaElementConfig_Type(type);
        for (SeaElementCache seaElementCache : list) {
            delete(seaElementCache);
        }
    }

    List<SeaElementCache> findByXAndY(Long x, Long y);
}

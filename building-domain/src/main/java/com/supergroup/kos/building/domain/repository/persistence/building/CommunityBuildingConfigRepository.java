package com.supergroup.kos.building.domain.repository.persistence.building;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import com.supergroup.core.repository.BaseJpaRepository;
import com.supergroup.kos.building.domain.model.config.CommunityBuildingConfig;

@Repository("CommunityBuildingConfigRepositoryJpa")
public interface CommunityBuildingConfigRepository extends BaseJpaRepository<CommunityBuildingConfig> {

    @Query("select c from CommunityBuildingConfig c where c.level = ?1")
    Optional<CommunityBuildingConfig> findByLevel(Long level);
    boolean existsByLevel(@NonNull Long level);

}

package com.supergroup.kos.building.domain.repository.persistence.building;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import com.supergroup.core.repository.BaseJpaRepository;
import com.supergroup.kos.building.domain.model.config.CastleConfig;

@Repository("CastleConfigRepositoryJpa")
public interface CastleConfigRepository extends BaseJpaRepository<CastleConfig> {

    @Query("select r from CastleConfig r where r.level = ?1")
    Optional<CastleConfig> findByLevel(Long level);
    boolean existsByLevel(@NonNull Long level);

}

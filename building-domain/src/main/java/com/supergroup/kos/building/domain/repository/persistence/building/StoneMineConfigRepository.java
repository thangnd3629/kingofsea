package com.supergroup.kos.building.domain.repository.persistence.building;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import com.supergroup.core.repository.BaseJpaRepository;
import com.supergroup.kos.building.domain.model.config.StoneMineConfig;

@Repository("StoneMineConfigRepositoryJpa")
public interface StoneMineConfigRepository extends BaseJpaRepository<StoneMineConfig> {

    @Query("select r from StoneMineConfig r where r.level = ?1")
    Optional<StoneMineConfig> findByLevel(Long level);
    boolean existsByLevel(@NonNull Long level);

}

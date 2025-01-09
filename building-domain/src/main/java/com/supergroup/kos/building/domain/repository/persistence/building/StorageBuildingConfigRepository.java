package com.supergroup.kos.building.domain.repository.persistence.building;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import com.supergroup.core.repository.BaseJpaRepository;
import com.supergroup.kos.building.domain.constant.BuildingName;
import com.supergroup.kos.building.domain.constant.StorageType;
import com.supergroup.kos.building.domain.model.building.StorageBuildingConfig;

@Repository("StorageBuildingConfigRepositoryJpa")
public interface StorageBuildingConfigRepository extends BaseJpaRepository<StorageBuildingConfig> {

    @Query("select s from StorageBuildingConfig s where s.name = ?1 and s.level = ?2")
    Optional<StorageBuildingConfig> findByNameAndLevel(BuildingName name, Long level);

    @Query("select s from StorageBuildingConfig s where s.name = ?1")
    List<StorageBuildingConfig> findByName(BuildingName name);
    boolean existsByLevel(@NonNull Long level);

    boolean existsByLevelAndType(@NonNull Long level, @NonNull StorageType type);

}

package com.supergroup.kos.building.domain.repository.persistence.building;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.supergroup.core.repository.BaseJpaRepository;
import com.supergroup.kos.building.domain.constant.StorageType;
import com.supergroup.kos.building.domain.model.building.StorageBuilding;

@Repository("StorageBuildingRepositoryJpa")
public interface StorageBuildingRepository extends BaseJpaRepository<StorageBuilding> {

    @Query("select s from StorageBuilding s where s.kosProfile.id = ?1 and s.storageType = ?2")
    Optional<StorageBuilding> findByKosProfileIdAndStorageType(Long id, StorageType storageType);

    @Query("select (count(s) > 0) from StorageBuilding s where s.kosProfile.id = ?1 and s.storageType = ?2")
    boolean existsByKosProfileIdAndStorageType(Long id, StorageType storageType);
}

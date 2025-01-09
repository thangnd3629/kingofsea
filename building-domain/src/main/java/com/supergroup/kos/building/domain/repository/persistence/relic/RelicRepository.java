package com.supergroup.kos.building.domain.repository.persistence.relic;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.supergroup.core.repository.BaseJpaRepository;
import com.supergroup.kos.building.domain.model.relic.Relic;

@Repository("RelicRepositoryJpa")
public interface RelicRepository extends BaseJpaRepository<Relic> {
    @Query("select r from Relic r where r.communityBuilding.kosProfile.id = ?1")
    Page<Relic> findByKosProfileId(Long id, Pageable pageable);

    @Query("select r from Relic r " +
           "where r.communityBuilding.kosProfile.id = ?1 " +
           "order by r.relicConfig.relicMpConfig.level DESC, r.relicConfig.name")
    List<Relic> find(Long id);

    @Query("select r from Relic r where r.communityBuilding.kosProfile.id = ?1 and r.isListing = true")
    List<Relic> findByKosProfileIdAndIsListingTrue(@NonNull Long id);

    @Query("select r from Relic r " +
           "where  r.communityBuilding.kosProfile.id = ?1 and r.isListing = ?2 " +
           "order by r.updatedAt DESC")
    List<Relic> find(Long id, Boolean isListing);

    Optional<Relic> findByIdAndCommunityBuilding_KosProfile_Id(@NonNull Long relicId, @NonNull Long kosProfileId);

    @Transactional
    @Modifying
    @Query("update Relic r set r.isListing = ?1 where r.id = ?2")
    void updateIsListingById(@NonNull Boolean isListing, @NonNull Long id);

    @Query("select r from Relic r where r.relicConfig.relicMpConfig.level = ?1 and r.communityBuilding.kosProfile.id = ?2")
    List<Relic> findByLevelAndKosProfileId(Long level, Long kosProfileId);

    @Query("select count(r) > 0 from Relic r where r.id = ?1 and r.status = com.supergroup.core.constant.BaseStatus.ACTIVATED")
    Boolean isActive(Long relicId);
}

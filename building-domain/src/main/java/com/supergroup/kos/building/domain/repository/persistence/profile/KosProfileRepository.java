package com.supergroup.kos.building.domain.repository.persistence.profile;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.supergroup.core.repository.BaseJpaRepository;
import com.supergroup.kos.building.domain.model.profile.KosProfile;

@Repository("KosProfileRepositoryJpa")
public interface KosProfileRepository extends BaseJpaRepository<KosProfile> {

    @Query("select k from KosProfile k where k.user.id = ?1")
    Optional<KosProfile> findByUserId(Long id);

    boolean existsByUser_Id(Long id);
    Optional<KosProfile> findByAssetsId(Long assetsId);

}

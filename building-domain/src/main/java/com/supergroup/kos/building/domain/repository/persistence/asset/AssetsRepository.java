package com.supergroup.kos.building.domain.repository.persistence.asset;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.supergroup.core.repository.BaseJpaRepository;
import com.supergroup.kos.building.domain.dto.asset.ITotalPeople;
import com.supergroup.kos.building.domain.model.asset.Assets;

@Repository("AssetsRepositoryJpa")
public interface AssetsRepository extends BaseJpaRepository<Assets> {
    Optional<Assets> findByKosProfile_Id(Long id);

    @Query("select (count(a) > 0) from Assets a where a.kosProfile.id = ?1")
    boolean existsByKosProfileId(Long id);

    @Query(value = "SELECT (castle.idle_people + stone.worker + wood.worker ) AS totalPeople \n"
                   + "FROM tbl_castle_building AS castle, tbl_stone_mine_building AS stone, tbl_wood_mine_building AS wood \n"
                   + "WHERE castle.kos_profile_id = :kos_profile_id AND stone.kos_profile_id = :kos_profile_id AND wood.kos_profile_id = :kos_profile_id"
            , nativeQuery = true)
    ITotalPeople getTotalPeople(@Param("kos_profile_id") Long kosProfileId);
}

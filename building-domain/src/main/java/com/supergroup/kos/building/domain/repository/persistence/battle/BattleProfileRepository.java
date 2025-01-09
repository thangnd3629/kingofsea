package com.supergroup.kos.building.domain.repository.persistence.battle;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.supergroup.core.repository.BaseJpaRepository;
import com.supergroup.kos.building.domain.constant.battle.FactionType;
import com.supergroup.kos.building.domain.model.battle.BattleProfile;

@Repository
public interface BattleProfileRepository extends BaseJpaRepository<BattleProfile> {

    @Query("SELECT bp FROM BattleProfile bp WHERE bp.kosProfile.id = :kosProfileId")
    Page<BattleProfile> findByKosProfileId(@Param("kosProfileId") Long kosProfileId, Pageable pageable);

    @Query("SELECT bp FROM BattleProfile bp WHERE bp.kosProfile.id = :kosProfileId AND bp.battle.id = :battleId")
    Optional<BattleProfile> findByKosProfileIdAndBattleId(@Param("kosProfileId") Long kosProfileId,
                                                          @Param("battleId") Long battleId);

    @Query("select b from BattleProfile b where b.battle.id = :id and b.kosProfile.id = :id1 and b.faction = :faction")
    Optional<BattleProfile> findByBattleIdAndKosProfileIdAndFaction(@Param("id") Long id, @Param("id1") Long id1, @Param("faction")
    FactionType faction);

    Set<BattleProfile> findByIdIn(Collection<Long> ids);



    
}

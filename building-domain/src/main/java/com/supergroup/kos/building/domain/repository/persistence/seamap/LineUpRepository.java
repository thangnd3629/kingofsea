package com.supergroup.kos.building.domain.repository.persistence.seamap;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.supergroup.core.repository.BaseJpaRepository;
import com.supergroup.kos.building.domain.constant.battle.FactionType;
import com.supergroup.kos.building.domain.model.seamap.ShipLineUp;

public interface LineUpRepository extends BaseJpaRepository<ShipLineUp> {
    @Query(value = "select s from ShipLineUp s where s.id =:shipLineUpId and"
                   + " s.motherShip.commandBuilding.kosProfile.id = :kosProfileId")
    Optional<ShipLineUp> getKosProfileLineUp(Long kosProfileId, Long shipLineUpId);

    Page<ShipLineUp> findByMotherShipIdOrderByUpdatedAtDesc(Long motherShipId, Pageable pageable);

    List<ShipLineUp> findByIdIn(Collection<Long> ids);

    @Query("select s from ShipLineUp s " +
           "where s.battleProfile.battle.id = ?1 and s.battleProfile.faction = ?2 and s.timeJoinedBattle > ?3")
    List<ShipLineUp> getLineupWaitingInBattle(Long id, FactionType faction,
                                              LocalDateTime timeJoinedBattle);

    @Query("select s from ShipLineUp s where s.battleProfile.battle.id = ?1 and s.battleProfile.faction = ?2")
    List<ShipLineUp> getListLineupInBattleByFaction(Long id, FactionType faction);

    @Query("select s from ShipLineUp s where s.battleProfile.battle.id = ?1")
    List<ShipLineUp> findByBattleId(Long id);

    @Query("select s from ShipLineUp s where s.battleProfile.id = :id and s.timeJoinedBattle > :timeJoinedBattle")
    List<ShipLineUp> findByBattleProfileIdAndTimeJoinedBattleAfter(@Param("id") Long id, @Param("timeJoinedBattle") LocalDateTime timeJoinedBattle);





}

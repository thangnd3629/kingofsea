package com.supergroup.kos.building.domain.repository.persistence.battle;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.supergroup.core.repository.BaseJpaRepository;
import com.supergroup.kos.building.domain.constant.battle.BattleStatus;
import com.supergroup.kos.building.domain.constant.battle.BattleType;
import com.supergroup.kos.building.domain.constant.battle.FactionType;
import com.supergroup.kos.building.domain.model.battle.Battle;

@Repository
public interface BattleRepository extends BaseJpaRepository<Battle> {

    @Query("SELECT COUNT(b) > 0 FROM Battle b LEFT JOIN BattleProfile bp ON bp.battle.id = b.id WHERE bp.kosProfile.id = :kosProfileId AND b.status IN :statuses AND bp.faction = :factionType")
    Boolean existByKosProfileIdAndBattleStatusAndFaction(@Param("kosProfileId") Long kosProfileId,
                                                         @Param("statuses") Collection<BattleStatus> statuses,
                                                         @Param("factionType") FactionType factionType);

    @Query("SELECT b FROM Battle b LEFT JOIN BattleProfile bp ON bp.battle.id = b.id "
           + "WHERE bp.kosProfile.id = :kosProfileId "
           + "AND b.status IN :statuses "
           + "AND (bp.faction = :factionType OR b.battleType = com.supergroup.kos.building.domain.constant.battle.BattleType.LIBERATE)"
           + "ORDER BY b.createdAt DESC")
    List<Battle> findByKosProfileIdAndBattleStatusAndFaction(@Param("kosProfileId") Long kosProfileId,
                                                             @Param("statuses") Collection<BattleStatus> statuses,
                                                             @Param("factionType") FactionType factionType);

    @Query("SELECT b FROM Battle b WHERE b.attacker.kosProfile.id = :kosProfileId "
           + "AND b.status IN :statuses "
           + "AND b.battleType = com.supergroup.kos.building.domain.constant.battle.BattleType.LIBERATE "
           + "ORDER BY b.createdAt DESC")
    List<Battle> findUserLiberateBattleByKosProfileIdAndBattleStatus(@Param("kosProfileId") Long kosProfileId,
                                                                     @Param("statuses") Collection<BattleStatus> statuses);

    @Query("SELECT b FROM Battle b WHERE b.battleField.id = :battleFieldId "
           + "AND b.status IN :statuses "
           + "AND b.battleType = com.supergroup.kos.building.domain.constant.battle.BattleType.LIBERATE "
           + "ORDER BY b.createdAt DESC")
    List<Battle> findUserLiberateBattleByBattleFieldIdAndBattleStatus(@Param("battleFieldId") Long battleFieldId,
                                                                      @Param("statuses") Collection<BattleStatus> statuses);

    @Query("SELECT b FROM Battle b LEFT JOIN BattleProfile bp ON bp.battle.id = b.id WHERE bp.kosProfile.id = :kosProfileId")
    List<Battle> findByKosProfileId(@Param("kosProfileId") Long kosProfileId);

    @Query("SELECT b FROM Battle b LEFT JOIN UserBase ub ON b.defender.kosProfile.base.id = ub.id WHERE ub.id = :userBaseId AND b.status IN :statuses")
    List<Battle> findByUserBaseIdAndStatus(@Param("userBaseId") Long userBaseId, @Param("statuses") Collection<BattleStatus> statuses);

    @Modifying
    @Query("update Battle b set b.attackerWithdrawAllNextRound = true where b.id= :battleId")
    void setAttackerWithdrawAll(Long battleId);

    @Query(value = "select countA  as countLineUpAttacker  ,count(lb.mother_ship_id) as countLineUpDefender \n"
                   + "from (select b.id as bid, b.battle_profile_defender_id as bpdi, count(la.mother_ship_id) as countA\n"
                   + "      from tbl_battle b\n"
                   + "               inner join tbl_battle_profile tap on b.battle_profile_attacker_id = tap.id and b.id = :battleId\n"
                   + "               left join tbl_ship_line_up la on tap.id = la.battle_profile_id\n"
                   + "      group by b.id) as countLineUpA\n"
                   + "         inner join tbl_battle_profile tbp on countLineUpA.bpdi = tbp.id\n"
                   + "         left join tbl_ship_line_up lb on tbp.id = lb.battle_profile_id\n"
                   + "group by countLineUpA.bid, countLineUpA.countA;", nativeQuery = true)
    CountLineUpInBattle countCommanderLineUp(Long battleId);

    interface CountLineUpInBattle {
        Long getCountLineUpAttacker();

        Long getCountLineUpDefender();
    }

    @Query("select (count(b) > 0) from Battle b " +
           "where b.attacker.kosProfile.id = ?1 and b.battleType = ?2 and b.status = ?3")
    boolean existsByAttackerKosProfileAndBattleTypeAndStatus(Long id, BattleType battleType, BattleStatus status);

    @Query("select (count(b) > 0) from Battle b " +
           "where b.attacker.kosProfile.id = ?1 and b.battleType = ?2 and b.status in ?3 and b.id <> ?4")
    boolean existsByAttackerKosProfileIdAndBattleTypeAndStatusInAndIdNot(Long id, BattleType battleType, Collection<BattleStatus> statuses,
                                                                         Long id1);

}

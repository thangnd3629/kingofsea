package com.supergroup.kos.building.domain.repository.persistence.battle;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.supergroup.core.repository.BaseJpaRepository;
import com.supergroup.kos.building.domain.model.battle.BattleReport;
import com.supergroup.kos.building.domain.model.battle.IBattleReport;

@Repository
public interface BattleReportRepository extends BaseJpaRepository<BattleReport> {

    @Query("SELECT br FROM BattleReport br WHERE br.battle.id = ?1 AND (br.initiator.kosProfile.id = ?2 OR br.victim.kosProfile.id = ?2)")
    Optional<BattleReport> findByBattleIdAndKosProfileId(Long battleId, Long kosProfileId);

    @Query(value = "select distinct tb.id as battleId, \n"
                   + "tb.battle_type as battleType,\n"
                   + "-- attacker \n"
                   + "attacker.id as attackerId,\n"
                   + "attacker.\"type\" as attackerType,\n"
                   + "attacker.kos_profile_id as attackerKosProfileId,\n"
                   + "attacker.x as attackerX,\n"
                   + "attacker.y as attackerY,\n"
                   + "attacker.username as attackerName,\n"
                   + "attacker.avatar as attackerAvatarUrl,\n"
                   + "-- defender\n"
                   + "defender.id as defenderId,\n"
                   + "defender.\"type\" as defenderType,\n"
                   + "defender.kos_profile_id as defenderKosProfileId,\n"
                   + "defender.x as defenderX,\n"
                   + "defender.y as defenderY,\n"
                   + "defender.username as defenderName,\n"
                   + "defender.avatar as defenderAvatarUrl,\n"
                   + "defender.boss_id as defenderBossId,\n"
                   + "defender.boss_config_id as defenderBossConfigId,\n"
                   + "\n"
                   + "tbr.winner_id as winnerId,\n"
                   + "tbr.loser_id as loserId,\n"
                   + "\n"
                   + "tbr.x as x,\n"
                   + "tbr.y as y,\n"
                   + "\n"
                   + "tbr.resource_type as resourceType,\n"
                   + "\n"
                   + "tb.status as status,\n"
                   + "\n"
                   + "tbr.start_at as startAt,\n"
                   + "tbr.end_at as endAt,\n"
                   + "tbr.updated_at as updatedAt,\n"
                   + "\n"
                   + "round_num.count as amountRound\n"
                   + "\n"
                   + "from tbl_user_battle_report tubr \n"
                   + "left join tbl_battle_profile tbp on tubr.battle_profile_id = tbp.id\n"
                   + "left join tbl_battle_report tbr on tbp.battle_report_id = tbr.id \n"
                   + "left join tbl_kos_profile tkp on tbp.kos_profile_id = tkp.id\n"
                   + "left join tbl_battle tb on tbr.battle_id = tb.id\n"
                   + "-- attacker \n"
                   + "left join tbl_battle_profile attacker on tbr.initiator_id = attacker.id\n"
                   + "left join tbl_kos_profile attacker_kos on attacker.kos_profile_id = attacker_kos.id\n"
                   + "left join tbl_element_sea attacker_element on attacker.boss_id = attacker_element.id\n"
                   + "-- defender\n"
                   + "left join tbl_battle_profile defender on tbr.victim_id = defender.id\n"
                   + "left join tbl_kos_profile defender_kos on defender.kos_profile_id = defender_kos.id\n"
                   + "left join tbl_element_sea defender_element on defender.boss_id = defender_element.id\n"
                   + "left join tbl_elements_config defender_element_config on defender_element.elements_config_id = defender_element_config.id\n"
                   + "\n"
                   + "-- round\n"
                   + "inner join (select tbr.battle_id, tbr.id, count(trr.id) \n"
                   + "\t\t\tfrom tbl_battle_report tbr\n"
                   + "\t\t\tleft join tbl_round_report trr on trr.battle_report_id = tbr.id\n"
                   + "\t\t\tgroup by tbr.id, tbr.battle_id) round_num on round_num.battle_id = tb.id\n"
                   + "\n"
                   + "where tkp.id = :kosProfileId\n"
                   + "and (attacker.kos_profile_id = :kosProfileId or defender.kos_profile_id = :kosProfileId)\n"
                   + "and (tubr.is_deleted = false or tubr.is_deleted is null)\n"
                   + "order by tbr.updated_at desc",
           nativeQuery = true)
    List<IBattleReport> findBattleReportByKosProfileId(@Param("kosProfileId") Long kosProfileId);

}

package com.supergroup.kos.building.domain.repository.persistence.battle;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.supergroup.core.repository.BaseJpaRepository;
import com.supergroup.kos.building.domain.constant.battle.BattleStatus;
import com.supergroup.kos.building.domain.model.battle.BattleReport;
import com.supergroup.kos.building.domain.model.battle.UserBattleReport;

@Repository
public interface UserBattleReportRepository extends BaseJpaRepository<UserBattleReport> {

    @Query("SELECT br FROM BattleReport br "
           + "LEFT JOIN UserBattleReport ubr ON ubr.battleReport.id = br.id "
           + "WHERE (br.initiator.kosProfile.id = :kosProfileId OR br.victim.kosProfile.id = :kosProfileId) "
           + "AND br.battle.status IN :battleStatuses "
           + "AND ubr.isDeleted = false "
           + "AND ubr.battleProfile.kosProfile.id = :kosProfileId "
           + "AND br.battle.currentRound >= 1 "
           + "GROUP BY br.id")
    Page<BattleReport> findByKosProfileIdAndBattleStatuses(@Param("kosProfileId") Long kosProfileId,
                                                           @Param("battleStatuses") Collection<BattleStatus> battleStatuses,
                                                           Pageable pageable);

    @Query("SELECT br FROM BattleReport br "
           + "LEFT JOIN UserBattleReport ubr ON ubr.battleReport.id = br.id "
           + "WHERE br.battle.id = :battleId "
           + "AND (br.initiator.kosProfile.id = :kosProfileId OR br.victim.kosProfile.id = :kosProfileId) "
           + "AND br.battle.status IN :battleStatuses "
           + "AND ubr.isDeleted = false "
           + "AND ubr.battleProfile.kosProfile.id = :kosProfileId "
           + "AND br.battle.currentRound >= 1 "
           + "GROUP BY br.id")
    Optional<BattleReport> findByKosProfileIdAndBattleIdAndBattleStatuses(@Param("kosProfileId") Long kosProfileId,
                                                                          @Param("battleId") Long battleId,
                                                                          @Param("battleStatuses") Collection<BattleStatus> battleStatuses);

    @Query("SELECT ubr FROM UserBattleReport ubr "
           + "LEFT JOIN BattleReport br ON br.id = ubr.battleReport.id "
           + "LEFT JOIN BattleProfile bp ON bp.id = ubr.battleProfile.id "
           + "WHERE br.battle.id = ?1 AND bp.kosProfile.id = ?2")
    Optional<UserBattleReport> findByBattleIdAndKosProfileId(Long battleId, Long kosProfileId);

    @Query("SELECT ubr FROM UserBattleReport ubr "
           + "LEFT JOIN BattleReport br ON br.id = ubr.battleReport.id "
           + "LEFT JOIN BattleProfile bp ON bp.id = ubr.battleProfile.id "
           + "WHERE br.battle.id = ?1 AND bp.kosProfile.id = ?2")
    List<UserBattleReport> findListUserBattleReportByBattleIdAndKosProfileId(Long battleId, Long kosProfileId);

    @Query(value = "select count(*) > 0 "
                   + "from tbl_user_battle_report tubr "
                   + "left join tbl_battle_profile tbp on tubr.battle_profile_id = tbp.id "
                   + "left join tbl_battle_report tbr on tbr.id = tubr.battle_report_id "
                   + "where tbp.kos_profile_id = ?1 and tbr.id = ?2",
           nativeQuery = true
    )
    Boolean existByBattleProfileIdAndBattleReportId(Long kosProfileId, Long battleReportId);
}

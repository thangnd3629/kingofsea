package com.supergroup.kos.building.domain.repository.persistence.battle;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.supergroup.core.repository.BaseJpaRepository;
import com.supergroup.kos.building.domain.model.battle.RoundReport;

@Repository
public interface RoundReportRepository extends BaseJpaRepository<RoundReport> {

    @Query("SELECT rr FROM RoundReport rr "
           + "LEFT JOIN BattleReport br ON rr.battleReport.id = br.id "
           + "LEFT JOIN BattleProfile bp ON bp.battleReport.id = br.id "
           + "WHERE rr.round.id = ?1 AND br.battle.id = ?2 AND bp.kosProfile.id = ?3")
    Optional<RoundReport> findByIdAndBattleIdAndKosProfileId(Long roundId, Long battleId, Long kosProfileId);
}

package com.supergroup.kos.building.domain.repository.persistence.seamap;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.supergroup.core.repository.BaseJpaRepository;
import com.supergroup.kos.building.domain.model.seamap.movesession.MissionType;
import com.supergroup.kos.building.domain.model.seamap.movesession.MoveSession;

@Repository
public interface MoveSessionRepository extends BaseJpaRepository<MoveSession> {
    @Query(value = "select m from MoveSession m where (m.isHidden is null or m.isHidden is false ) and m.seaActivity.kosProfile.id = :kosProfileId order by m.timeStart desc ")
    Page<MoveSession> getAllTroopMovement(Long kosProfileId, Pageable pageable);

    Optional<MoveSession> getBySeaActivityKosProfileIdAndId(Long kosProfileId, Long id);

    @Query(value = "select count(*) from tbl_move_session as mv inner join tbl_activity_sea tas on mv.id = tas.active_move_session_id\n"
                   + "\n"
                   + "where mv.is_processed is false and mv.mission_type = :#{#missionType.name()} and tas.kos_profile_id = :kosProfileId and mv.destination_element_id = :destinationElementId\n",
           nativeQuery = true)
    Integer existsInProgressMoveWithMissionToTarget(MissionType missionType, Long destinationElementId, Long kosProfileId);

    @Query(value = "select m.* from tbl_activity_sea a inner join tbl_move_session m "
                   + "on a.active_move_session_id = m.id "
                   + "where a.kos_profile_id <> :kosTargetId and m.destination_element_id = :destinationElementId "
                   + "and a.status in ('MOVING') "
                   + "and m.mission_type not in ('SCOUT', 'RETURN', 'STATION')",
           nativeQuery = true)
    List<MoveSession> findMovesToTarget(Long destinationElementId, Long kosTargetId);
}

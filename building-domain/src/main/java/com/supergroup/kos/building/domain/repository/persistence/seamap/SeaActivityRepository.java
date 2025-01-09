package com.supergroup.kos.building.domain.repository.persistence.seamap;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.supergroup.core.repository.BaseJpaRepository;
import com.supergroup.kos.building.domain.constant.MissionType;
import com.supergroup.kos.building.domain.constant.seamap.SeaActivityStatus;
import com.supergroup.kos.building.domain.model.seamap.Coordinates;
import com.supergroup.kos.building.domain.model.seamap.SeaActivity;

@Repository
public interface SeaActivityRepository extends BaseJpaRepository<SeaActivity> {
    @Query(value = "select a from SeaActivity a where a.kosProfile.id = :kosProfileId and (a.isDeleted is null or a.isDeleted is false)")
    List<SeaActivity> findActiveActivities(Long kosProfileId);

    @Query(value = "select count(a) from SeaActivity a where a.kosProfile.id = :kosProfileId and (a.isDeleted is null or a.isDeleted is false)")
    Long countActiveActivities(Long kosProfileId);

    List<SeaActivity> findByCurrentLocation(Coordinates position);

    @Query(value = "select a from SeaActivity a where a.currentLocation is null and (a.isDeleted is null or a.isDeleted is false)")
    List<SeaActivity> findMovingActivity();

    @Query(value = "select m.seaActivity from MoveSession m where m.id= :moveSessionId")
    SeaActivity getActivityByMoveSession(Long moveSessionId);

    @Query("SELECT s FROM SeaActivity s WHERE s.stationAt.id = ?1 AND s.kosProfile.id = ?2")
    List<SeaActivity> findByElementIdAndKosProfileId(Long elementId, Long kosProfileId);

    @Query("select s from SeaActivity s where s.stationAt.id = :id")
    List<SeaActivity> findByStationAt(@Param("id") Long id);


    @Query(value = "select count(*) from tbl_activity_sea ac where ac.station_id = :elementId and ac.kos_profile_id = :kosProfileId", nativeQuery = true)
    Integer countStationsActivity(Long elementId, Long kosProfileId);

    @Query("select s from SeaActivity s where s.stationAt.id = ?1 and s.status = ?2")
    List<SeaActivity> findByStationAtAndStatus(Long id, SeaActivityStatus status);


}

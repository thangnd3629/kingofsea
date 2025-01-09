package com.supergroup.kos.building.domain.repository.persistence.scout;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.supergroup.core.repository.BaseJpaRepository;
import com.supergroup.kos.building.domain.constant.MissionStatus;
import com.supergroup.kos.building.domain.model.scout.Scout;

@Repository("ScoutRepositoryJpa")
public interface ScoutRepository extends BaseJpaRepository<Scout>  {
    @Query("select count(s) from Scout s where s.scouter.id = :id and s.missionStatus = :missionStatus")
    long countByScouterIdAndMissionStatus(@Param("id") Long id, @Param("missionStatus") MissionStatus missionStatus);

}

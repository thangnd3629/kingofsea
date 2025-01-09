package com.supergroup.kos.building.domain.repository.persistence.seamap;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.supergroup.core.repository.BaseJpaRepository;
import com.supergroup.kos.building.domain.model.battle.OccupationBase;
import com.supergroup.kos.building.domain.model.seamap.Coordinates;
import com.supergroup.kos.building.domain.model.seamap.UserBase;

@Repository
public interface UserBaseRepository extends BaseJpaRepository<UserBase> {
    Optional<UserBase> findUserBaseByKosProfileId(Long kosProfileId);

    Optional<UserBase> findFirstByActiveAndIsReadyAndKosProfileIsNullOrderByIdAsc(Boolean active, Boolean isReady);

    List<UserBase> findByActive(Boolean active);

    List<UserBase> findByIsReady(Boolean isReady);

    List<UserBase> findByActiveAndIsReady(Boolean active, Boolean isReady);

    List<UserBase> findByCoordinatesAndActive(Coordinates coordinates, Boolean active);

    @Query(value = "select ub.id as elementId, ub.seaElementConfig.id as elementConfigId, ub.coordinates.x as x, ub.coordinates.y as y, "
                   + " ub.invader.occupyAt as timeStart, ub.islandName as name, cb.level as level, up.avatarUrl as avatarUrl "
                   + " from UserBase ub "
                   + " left join KosProfile kp on ub.kosProfile.id = kp.id "
                   + " left join CastleBuilding cb on ub.kosProfile.id = cb.kosProfile.id "
                   + " left join UserProfile up on kp.user.id = up.user.id "
                   + " where ub.invader.kosProfileInvader.id = :kosProfileId ")
    List<OccupationBase> getListOccupations(@Param("kosProfileId") Long kosProfileId);

    List<UserBase> findByIdIn(Collection<Long> ids);


}

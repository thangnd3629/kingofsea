package com.supergroup.kos.building.domain.repository.persistence.technology;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.supergroup.core.repository.BaseJpaRepository;
import com.supergroup.kos.building.domain.constant.TechnologyCode;
import com.supergroup.kos.building.domain.constant.TechnologyType;
import com.supergroup.kos.building.domain.model.technology.UserTechnology;

@Repository("UserTechnologyRepositoryJpa")
public interface UserTechnologyRepository extends BaseJpaRepository<UserTechnology> {
    @Query("select u from UserTechnology u where u.researchBuilding.kosProfile.id = ?1 and u.technology.code = ?2")
    Optional<UserTechnology> findByKosProfileIdAndTechnologyCode(Long id, TechnologyCode code);

    @Query("select u from UserTechnology u where u.researchBuilding.kosProfile.id = ?1 and u.technology.technologyType = ?2 order by u.id")
    List<UserTechnology> findByKosProfileIdAndTechnologyType(Long id, TechnologyType technologyType);
}

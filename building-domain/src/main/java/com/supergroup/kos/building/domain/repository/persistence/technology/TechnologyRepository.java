package com.supergroup.kos.building.domain.repository.persistence.technology;

import java.util.List;
import java.util.Optional;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import com.supergroup.core.repository.BaseJpaRepository;
import com.supergroup.kos.building.domain.constant.TechnologyCode;
import com.supergroup.kos.building.domain.constant.TechnologyType;
import com.supergroup.kos.building.domain.model.technology.Technology;

@Repository("TechnologyRepositoryJpa")
public interface TechnologyRepository extends BaseJpaRepository<Technology> {

    List<Technology> findByTechnologyType(TechnologyType technologyType);

    Optional<Technology> findByCode(@NonNull TechnologyCode code);
}

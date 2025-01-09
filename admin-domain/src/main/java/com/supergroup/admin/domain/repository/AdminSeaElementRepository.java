package com.supergroup.admin.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.supergroup.core.repository.BaseJpaRepository;
import com.supergroup.kos.building.domain.model.seamap.SeaElement;

@Repository("AdminSeaElementRepositoryJpa")
public interface AdminSeaElementRepository<T extends SeaElement> extends BaseJpaRepository<T> {

    @Query("select s from SeaElement s "
           + "where s.coordinates.x >= :xLeft "
           + "and s.coordinates.x <= :xRight "
           + "and s.coordinates.y >= :yBelow "
           + "and s.coordinates.y <= :yTop "
           + "and s.active = 'true' ")
    List<SeaElement> getElementsInArea(Long xLeft, Long xRight, Long yBelow, Long yTop);
}

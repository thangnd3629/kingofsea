package com.supergroup.kos.building.domain.repository.persistence.scout;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.supergroup.core.repository.BaseJpaRepository;
import com.supergroup.kos.building.domain.model.scout.ScoutReport;

@Repository("ScoutReportRepositoryJpa")
public interface ScoutReportRepository extends BaseJpaRepository<ScoutReport> {
    @Query("select s from ScoutReport s " +
           "where s.kosProfile.id = ?1 " +
           "and s.active = 'true' " +
           "order by s.isBookmark DESC, s.id DESC ")
    Page<ScoutReport> getAllReport(Long id, Pageable pageable);

    List<ScoutReport> findByScout_Id(Long id);
}

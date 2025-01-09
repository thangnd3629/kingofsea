package com.supergroup.kos.building.domain.repository.persistence.seamap;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.supergroup.core.repository.BaseJpaRepository;
import com.supergroup.kos.building.domain.constant.seamap.BossSeaStatus;
import com.supergroup.kos.building.domain.model.seamap.BossSea;

@Repository("BossSeaElementRepositoryJpa")
public interface BossSeaElementRepository extends BaseJpaRepository<BossSea> {

    @Query("SELECT b FROM BossSea b WHERE b.status IN :statues")
    List<BossSea> findByStatuses(@Param("statues") Collection<BossSeaStatus> statuses);

    List<BossSea> findByIdIn(Collection<Long> ids);

}

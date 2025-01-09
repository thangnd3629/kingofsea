package com.supergroup.kos.building.domain.repository.persistence.seamap;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.supergroup.core.repository.BaseJpaRepository;
import com.supergroup.kos.building.domain.model.seamap.KosWarInfo;

@Service
public interface KosWarInfoRepository extends BaseJpaRepository<KosWarInfo> {
    Optional<KosWarInfo> findByKosProfileId(Long kosProfileId);

    List<KosWarInfo> findByKosProfile_IdIn(Collection<Long> ids);

    boolean existsByKosProfile_Id(Long id);



}

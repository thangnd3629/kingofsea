package com.supergroup.auth.domain.repository.persistence;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.supergroup.auth.domain.constant.VerifyReason;
import com.supergroup.auth.domain.model.VerifySession;
import com.supergroup.core.repository.BaseJpaRepository;

@Repository("VerifySessionRepositoryJpa")
public interface VerifySessionRepository extends BaseJpaRepository<VerifySession> {

    Optional<VerifySession> findByIdAndReason(Long id, VerifyReason reason);

    Optional<VerifySession> findByAccountIdAndReason(Long accountId, VerifyReason reason);



}

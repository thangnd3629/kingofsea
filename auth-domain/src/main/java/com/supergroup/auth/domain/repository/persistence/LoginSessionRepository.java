package com.supergroup.auth.domain.repository.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.supergroup.auth.domain.model.LoginSession;
import com.supergroup.core.repository.BaseJpaRepository;

@Repository("LoginSessionRepositoryJpa")
public interface LoginSessionRepository extends BaseJpaRepository<LoginSession> {

    @Transactional
    @Modifying
    @Query("delete from LoginSession l where l.user.id = ?1")
    int deleteByUserId(Long userId);

    List<LoginSession> findByUserId(Long userId);

    Optional<LoginSession> findFirstByUser_IdOrderByUpdatedAtDesc(Long id);

    boolean existsByUuid(String uuid);

}

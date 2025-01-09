package com.supergroup.auth.domain.repository.persistence;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.supergroup.auth.domain.model.User;
import com.supergroup.core.repository.BaseJpaRepository;

@Repository("UserRepositoryJpa")
public interface UserRepository extends BaseJpaRepository<User> {

    Optional<User> findByEmail(String email);

    Optional<User> findByOriginEmail(String originEmail);

    boolean existsByOriginEmail(String originEmail);
}

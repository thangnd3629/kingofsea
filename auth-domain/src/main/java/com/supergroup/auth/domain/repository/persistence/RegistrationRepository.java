package com.supergroup.auth.domain.repository.persistence;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.supergroup.auth.domain.model.Registration;
import com.supergroup.core.repository.BaseJpaRepository;

@Repository("RegistrationRepositoryJpa")
public interface RegistrationRepository extends BaseJpaRepository<Registration> {

    Optional<Registration> findRegistrationByEmail(String email);
}

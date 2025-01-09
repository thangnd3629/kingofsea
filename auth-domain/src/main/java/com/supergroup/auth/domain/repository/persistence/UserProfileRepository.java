package com.supergroup.auth.domain.repository.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.supergroup.auth.domain.model.UserProfile;
import com.supergroup.core.repository.BaseJpaRepository;

/**
 * Copyright 2021 {@author Loda} (https://loda.me).
 * This project is licensed under the MIT license.
 */
@Repository("UserProfileRepositoryJpa")
public interface UserProfileRepository extends BaseJpaRepository<UserProfile> {

    @Query("select u from UserProfile u where u.user.id = ?1")
    Optional<UserProfile> findByUserId(Long id);

}

package com.supergroup.admin.domain.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.supergroup.auth.domain.constant.UserStatus;
import com.supergroup.auth.domain.model.User;
import com.supergroup.core.repository.BaseJpaRepository;

@Repository
public interface UserRepositoryAdmin extends BaseJpaRepository<User> {
    Optional<User> findByEmail(String email);

    Page<User> findByUserStatus(UserStatus userStatus, Pageable pageable);



}

package com.supergroup.admin.domain.repository;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.supergroup.admin.domain.model.AdminAccount;
import com.supergroup.core.repository.BaseJpaRepository;
@Repository
public interface AdminAccountRepository extends BaseJpaRepository<AdminAccount> {
    Optional<AdminAccount> findByUsername(String username);
}

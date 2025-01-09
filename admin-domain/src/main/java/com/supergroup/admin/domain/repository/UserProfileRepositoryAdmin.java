package com.supergroup.admin.domain.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.supergroup.auth.domain.model.UserProfile;
import com.supergroup.core.repository.BaseJpaRepository;
@Repository
public interface UserProfileRepositoryAdmin extends BaseJpaRepository<UserProfile> {
    Optional<UserProfile> findByUser_Id(Long id);

    List<UserProfile> findByUser_IdIn(Collection<Long> ids);


}

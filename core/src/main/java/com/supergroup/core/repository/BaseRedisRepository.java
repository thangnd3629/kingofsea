package com.supergroup.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface BaseRedisRepository<T, ID> extends JpaRepository<T, ID> {

}

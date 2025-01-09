package com.supergroup.core.repository;

import java.util.Optional;

import com.supergroup.core.constant.ConfigKey;
import com.supergroup.core.model.Config;

public interface ConfigRepository extends BaseJpaRepository<Config> {
    Optional<Config> findByKey(ConfigKey key);
}

package com.supergroup.core.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.supergroup.core.model.Config;
import com.supergroup.core.model.ConfigCache;

@Mapper
public interface ConfigMapper {

    @Mapping(target = "key", expression = "java(com.supergroup.core.constant.ConfigKey.valueOf(config.getKey()))")
    Config toConfig(ConfigCache config);

    @Mapping(target = "key", expression = "java(config.getKey().getName())")
    ConfigCache toConfigCache(Config config);
}

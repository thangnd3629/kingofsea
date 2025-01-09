package com.supergroup.kos.upgrading.consumer;

import java.util.Objects;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class JedisConfig {

    @Bean
    public static RedisConnection getRedisConnection(RedisTemplate<String, Object> redisTemplate) {
        return Objects.requireNonNull(redisTemplate.getConnectionFactory()).getConnection();
    }

    @Bean
    public StringRedisSerializer getStringRedisSerializer() {
        return new StringRedisSerializer();
    }

    @Bean
    public GenericJackson2JsonRedisSerializer getJsonSerializer(ObjectMapper objectMapper) {
        return new GenericJackson2JsonRedisSerializer(new ObjectMapper().enableDefaultTyping(DefaultTyping.NON_FINAL, As.PROPERTY));
    }

    @Bean
    public RedisConnectionFactory redisConnectionFactory(RedisProperties properties) {

        var config = new RedisStandaloneConfiguration();
        config.setHostName(properties.getHost());
        config.setPassword(properties.getPassword());
        config.setPort(properties.getPort());

        var poolConfig = new GenericObjectPoolConfig<>();
        poolConfig.setMaxIdle(20);
        poolConfig.setMinIdle(6);
        poolConfig.setMaxTotal(200);
        var clientConfig = JedisClientConfiguration.builder()
                                                   .usePooling()
                                                   .poolConfig(poolConfig)
                                                   .build();

        return new JedisConnectionFactory(config, clientConfig);
    }

    public RedisTemplate<String, Object> redisTemplate(StringRedisSerializer stringSerializer, RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(stringSerializer);
        return template;
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplateWithJsonSerializer(StringRedisSerializer stringSerializer,
                                                                         GenericJackson2JsonRedisSerializer jsonRedisSerializer,
                                                                         RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(stringSerializer);
        template.setHashValueSerializer(jsonRedisSerializer);
        template.setValueSerializer(jsonRedisSerializer);
        return template;
    }
}
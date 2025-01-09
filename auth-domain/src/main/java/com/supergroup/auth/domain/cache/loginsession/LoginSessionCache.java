package com.supergroup.auth.domain.cache.loginsession;

import javax.persistence.Id;

import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(chain = true)
@RedisHash("LoginSession")
public class LoginSessionCache {
    /**
     * login session id
     */
    @Id
    private String uuid;
    @Indexed
    private Long   userId;
}

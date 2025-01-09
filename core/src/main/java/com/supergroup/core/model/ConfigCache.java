package com.supergroup.core.model;

import java.io.Serializable;

import javax.persistence.Id;

import org.springframework.data.redis.core.index.Indexed;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ConfigCache implements Serializable {
    @Id
    private Long   id;
    @Indexed
    private String key;
    private String value;
}

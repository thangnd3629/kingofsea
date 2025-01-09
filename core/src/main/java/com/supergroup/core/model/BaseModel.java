package com.supergroup.core.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.Lob;
import javax.persistence.MappedSuperclass;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@MappedSuperclass
@Getter
@Setter
@ToString
@NoArgsConstructor
@Accessors(chain = true)
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseModel implements Serializable {

    @Column(name = "system_status", nullable = false, columnDefinition = "int default 0")
    protected int           systemStatus;
    @Lob
    protected byte[]        metadata;
    @CreatedDate
    @Column(updatable = false)
    private   LocalDateTime createdAt;
    @LastModifiedDate
    private   LocalDateTime updatedAt;
}

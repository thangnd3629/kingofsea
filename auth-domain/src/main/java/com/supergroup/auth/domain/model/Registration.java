package com.supergroup.auth.domain.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

import com.supergroup.core.model.BaseModel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Entity
@Table(name = "tbl_registration", indexes = @Index (columnList = "email, originEmail"))
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class Registration extends BaseModel implements Verifiable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long   id;
    @Column(nullable = false)
    private String email;
    @Column(nullable = false)
    private String originEmail;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private String username;

    @Override
    public Long getAccountId() {
        return id;
    }
}

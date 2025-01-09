package com.supergroup.admin.dto;

import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class InitUser {
    @NotNull
    private Long userId;
    private Long newKosProfileId;
}

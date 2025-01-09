package com.supergroup.admin.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class AdminUpdateUserTechnologyRequest {
    private Boolean isResearched;
    private Boolean isLock;
}

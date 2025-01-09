package com.supergroup.admin.domain.dto.request;

import javax.validation.constraints.Min;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminUpdateBuildingRequest {
    @Min(0)
    private Long level;
}

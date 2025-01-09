package com.supergroup.admin.dto;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminUpdateCommunityBuildingRequest {
    @Max(7)
    @Min(1)
    private Long maxListingRelic;
}

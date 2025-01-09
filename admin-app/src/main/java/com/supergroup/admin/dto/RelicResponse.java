package com.supergroup.admin.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class RelicResponse {
    private Long                id;
    private RelicConfigResponse model;
    private Boolean             isListing;
}

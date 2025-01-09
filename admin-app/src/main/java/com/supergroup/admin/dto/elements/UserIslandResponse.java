package com.supergroup.admin.dto.elements;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class UserIslandResponse extends ElementResponse {
    private Long   kosProfileId;
    private String name;
    private Long   level;
}

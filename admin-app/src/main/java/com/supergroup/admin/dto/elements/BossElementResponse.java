package com.supergroup.admin.dto.elements;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class BossElementResponse extends ElementResponse {
    private Long   hpLost;
    private String status;
    private Long   timeReviving;
}

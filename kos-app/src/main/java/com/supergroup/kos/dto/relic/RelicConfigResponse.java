package com.supergroup.kos.dto.relic;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class RelicConfigResponse {
    private Long   id;
    private String name;
    private Long   level;
    private String thumbnail;
    private Long   mp;

}

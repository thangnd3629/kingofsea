package com.supergroup.kos.dto.seamap.alliance;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class AllianceInfoResponse {
    private Long   id;
    private String description;
}

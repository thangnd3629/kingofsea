package com.supergroup.kos.dto.seamap.mining;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResourceElementDTO {
    private Long                  id;
    private Long                  elementId;
    private MiningSessionResponse miningSession;
}

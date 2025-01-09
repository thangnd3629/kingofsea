package com.supergroup.kos.building.domain.dto.battle;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class VaultProtectedResource {
    private Long gold = 0L;
    private Long stone = 0L;
    private Long wood = 0L;
    private Long relics = 0L;
    private Long queens = 0L;
    private Long weapons = 0L;
}

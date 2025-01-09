package com.supergroup.kos.building.domain.command;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class WithdrawActivityCommand {
    private Long id;
    private Boolean forceReturnOwnedBase;
}

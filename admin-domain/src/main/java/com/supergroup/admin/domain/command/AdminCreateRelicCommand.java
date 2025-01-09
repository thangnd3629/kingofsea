package com.supergroup.admin.domain.command;

import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class AdminCreateRelicCommand {
    @NotNull(message = "kosProfileId is required.")
    private Long kosProfileId;
    @NotNull(message = "relicConfigId is required.")
    private Long relicModelId;
}

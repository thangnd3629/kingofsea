package com.supergroup.admin.domain.command;

import javax.validation.constraints.Min;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminUpdatePointCommand {
    @Min(0)
    private Long mp;
    @Min(0)
    private Long gp;
    @Min(0)
    private Long tp;
}

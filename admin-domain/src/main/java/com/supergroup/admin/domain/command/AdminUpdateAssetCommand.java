package com.supergroup.admin.domain.command;

import javax.validation.constraints.Min;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminUpdateAssetCommand {

    @Min(0)
    private Double wood;
    @Min(0)
    private Double stone;
    @Min(0)
    private Double gold;
    @Min(0)
    private Double people;

}

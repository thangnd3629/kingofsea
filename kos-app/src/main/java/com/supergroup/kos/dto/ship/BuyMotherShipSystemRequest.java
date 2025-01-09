package com.supergroup.kos.dto.ship;

import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BuyMotherShipSystemRequest {
    @NotNull
    private Long modelId;
}

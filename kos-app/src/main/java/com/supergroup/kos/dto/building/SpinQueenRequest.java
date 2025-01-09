package com.supergroup.kos.dto.building;

import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SpinQueenRequest {
    @NotNull(message = "Number of queen card is not null")
    Integer numberOfQueenCard;
}

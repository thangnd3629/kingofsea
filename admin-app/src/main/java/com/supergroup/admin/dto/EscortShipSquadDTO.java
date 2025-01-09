package com.supergroup.admin.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@Accessors(chain = true)
public class EscortShipSquadDTO {
    @JsonProperty("initialAmount")
    private Long               amount;
    private EscortShipResponse escortShip;
}

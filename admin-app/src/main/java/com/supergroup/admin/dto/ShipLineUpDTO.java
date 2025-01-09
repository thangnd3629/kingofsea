package com.supergroup.admin.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@Accessors(chain = true)
public class ShipLineUpDTO {
    private Long                     id;
    private String                   motherShipThumbnail;
    private Long                     numberArmy;
    private Long                     shipUnits;
    private MotherShipResponse       motherShip;
    private List<EscortShipSquadDTO> escortShipSquad;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime            updatedAt;
}

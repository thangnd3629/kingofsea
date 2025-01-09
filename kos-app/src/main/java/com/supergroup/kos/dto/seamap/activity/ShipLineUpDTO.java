package com.supergroup.kos.dto.seamap.activity;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.supergroup.kos.dto.ship.MotherShipResponse;

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
    private Long                     shipUnits = 0L;
    private MotherShipResponse       motherShip;
    private List<EscortShipSquadDTO> escortShips;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime            updatedAt;
}

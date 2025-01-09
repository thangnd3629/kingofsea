package com.supergroup.kos.dto.battle;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class ShipReportResponse {
    private List<MotherShipReportResponse> motherShips;
    private List<EscortShipReportResponse> escortShip;
    private List<MotherShipReportResponse> reserveMotherShips;
    private List<EscortShipReportResponse> reserveEscortShips;
    private List<UsedItemResponse>         usedItem;
    private BossReportResponse             boss;
}

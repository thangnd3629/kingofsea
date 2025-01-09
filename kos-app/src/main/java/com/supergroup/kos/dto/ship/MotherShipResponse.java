package com.supergroup.kos.dto.ship;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.supergroup.kos.building.domain.constant.seamap.SeaActivityStatus;
import com.supergroup.kos.building.domain.model.seamap.Coordinates;
import com.supergroup.kos.dto.technology.TechnologyDTO;
import com.supergroup.kos.dto.weapon.WeaponResponse;
import com.supergroup.kos.dto.weapon.WeaponSetResponse;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class MotherShipResponse {
    private Long                            id;
    private Long                            maxSlotWeaponOfMotherShip;
    private TechnologyDTO                   technologyUnlockSlot;
    private MotherShipConfigResponse        model;
    private MotherShipLevelConfigResponse   levelInfo;
    private MotherShipQualityConfigResponse qualityInfo;
    private List<WeaponResponse>            weapons;
    private List<WeaponSetResponse>         weaponSets;
    private SeaActivityStatus               status;
    private Double                          power;
    private Coordinates                     currentLocation;
    private Long                            currentHp;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime                   arrivalMainBaseTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime                   lastTimeCalculateHp;
    private Coordinates                     returnLocation;
    private Boolean                         isHealing;
}

package com.supergroup.kos.building.domain.model.upgrade;

import java.io.Serializable;

import javax.persistence.Embeddable;

import com.supergroup.kos.building.domain.constant.BuildingName;
import com.supergroup.kos.building.domain.constant.UpgradeMotherShipType;
import com.supergroup.kos.building.domain.constant.UpgradeType;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@Embeddable
public class InfoInstanceModel implements Serializable {
    private Long                  instanceId; // id building, ship,...
    private UpgradeType           type;
    private BuildingName          buildingName;
    private UpgradeMotherShipType upgradeMotherShipType;
    private Long                  parameter; // scout straining,...

}

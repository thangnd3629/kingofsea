package com.supergroup.kos.building.domain.service.upgrade;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.supergroup.kos.building.domain.constant.UpgradeMotherShipType;
import com.supergroup.kos.building.domain.constant.UpgradeType;
import com.supergroup.kos.building.domain.model.building.BaseBuilding;
import com.supergroup.kos.building.domain.model.ship.EscortShip;
import com.supergroup.kos.building.domain.model.ship.MotherShip;
import com.supergroup.kos.building.domain.model.upgrade.InfoInstanceModel;
import com.supergroup.kos.building.domain.model.upgrade.UpgradeSession;
import com.supergroup.kos.building.domain.repository.persistence.upgrade.UpgradeSessionRepository;

import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;

@Service
@RequiredArgsConstructor
public class UpgradeSessionService {
    @Delegate
    private final UpgradeSessionRepository upgradeSessionRepository;

    public void save(UpgradeSession upgradeSession) {
        upgradeSessionRepository.save(upgradeSession);
    }

    public UpgradeSession createUpgradeBuildingSession(BaseBuilding building, LocalDateTime timeStart, Long duration) {
        UpgradeSession upgradeSession = new UpgradeSession();
        upgradeSession.setKosProfile(building.getKosProfile())
                      .setTimeStart(timeStart)
                      .setDuration(duration)
                      .setInfoInstanceModel(new InfoInstanceModel().setBuildingName(building.getName())
                                                                   .setType(UpgradeType.BUILDING));
        return upgradeSessionRepository.save(upgradeSession);
    }

    public UpgradeSession createUpgradeMotherShipSession(MotherShip motherShip, UpgradeMotherShipType upgradeMotherShipType, LocalDateTime timeStart,
                                                         Long duration) {
        UpgradeSession upgradeSession = new UpgradeSession();
        upgradeSession.setKosProfile(motherShip.getCommandBuilding().getKosProfile())
                      .setTimeStart(timeStart)
                      .setDuration(duration)
                      .setInfoInstanceModel(new InfoInstanceModel().setInstanceId(motherShip.getId())
                                                                   .setType(UpgradeType.MOTHER_SHIP)
                                                                   .setUpgradeMotherShipType(upgradeMotherShipType));
        return upgradeSessionRepository.save(upgradeSession);
    }

    public UpgradeSession createEscortShipSession(EscortShip escortShip, LocalDateTime timeStart, Long duration, UpgradeType upgradeType) {
        UpgradeSession upgradeSession = new UpgradeSession();
        upgradeSession.setKosProfile(escortShip.getEscortShipGroup().getAssets().getKosProfile())
                      .setTimeStart(timeStart)
                      .setDuration(duration)
                      .setInfoInstanceModel(new InfoInstanceModel().setInstanceId(escortShip.getId())
                                                                   .setType(upgradeType));
        return upgradeSessionRepository.save(upgradeSession);
    }
}

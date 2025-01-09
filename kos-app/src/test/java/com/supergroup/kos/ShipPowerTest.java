package com.supergroup.kos;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.supergroup.core.constant.ErrorCode;
import com.supergroup.core.exception.KOSException;
import com.supergroup.kos.building.domain.model.config.battle.DefBattleConfig;
import com.supergroup.kos.building.domain.model.seamap.EscortShipSquad;
import com.supergroup.kos.building.domain.model.ship.EscortShip;
import com.supergroup.kos.building.domain.repository.persistence.ship.EscortShipRepository;
import com.supergroup.kos.building.domain.service.battle.BattleUnitService;
import com.supergroup.kos.building.domain.service.config.KosConfigService;
import com.supergroup.kos.building.domain.service.ship.EscortShipService;
import com.supergroup.kos.building.domain.service.ship.MotherShipService;

@SpringBootTest()
@Transactional
public class ShipPowerTest {
    @Autowired
    private BattleUnitService shipBattleService;
    @Autowired
    private MotherShipService motherShipService;
    @Autowired

    private EscortShipService escortShipService;
    @Autowired
    private EscortShipRepository escortShipRepository;
    @Autowired
    private KosConfigService kosConfigService;

    @Test
    public void testEscortShip() throws InterruptedException {
        EscortShip escortShip = escortShipRepository.findById(119L).orElseThrow(()-> KOSException.of(ErrorCode.SERVER_ERROR));
        DefBattleConfig defBattleConfig = kosConfigService.getDefBattleConfig();
        shipBattleService.toEscortShipBattle(new EscortShipSquad().setEscortShip(escortShip), defBattleConfig);

    }
}

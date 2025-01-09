package com.supergroup.kos.battle;

import java.io.IOException;
import java.util.List;

import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.supergroup.kos.building.domain.constant.battle.BattleType;
import com.supergroup.kos.building.domain.model.battle.Battle;
import com.supergroup.kos.building.domain.model.battle.BattleProfile;
import com.supergroup.kos.building.domain.model.battle.BattleRound;
import com.supergroup.kos.building.domain.model.battle.BattleRoundSnapshot;
import com.supergroup.kos.building.domain.model.seamap.ShipLineUp;
import com.supergroup.kos.building.domain.model.seamap.UserBase;
import com.supergroup.kos.building.domain.repository.persistence.battle.BattleRepository;
import com.supergroup.kos.building.domain.repository.persistence.battle.BattleRoundRepository;
import com.supergroup.kos.building.domain.repository.persistence.battle.RoundReportRepository;
import com.supergroup.kos.building.domain.repository.persistence.seamap.SeaActivityRepository;
import com.supergroup.kos.building.domain.repository.persistence.seamap.UserBaseRepository;
import com.supergroup.kos.building.domain.service.battle.BattlePvPService;
import com.supergroup.kos.building.domain.service.battle.InitBattleService;

@SpringBootTest()
//@Transactional
public class CombatTest {
    @Autowired
    private BattlePvPService  battlePvPService;
    @Autowired
    private InitBattleService initBattleService;

    @Autowired
    private SeaActivityRepository seaActivityRepository;

    @Autowired
    private UserBaseRepository    userBaseRepository;
    @Autowired
    private BattleRoundRepository battleRoundRepository;

    @Autowired
    private RoundReportRepository roundReportRepository;

    @Autowired
    private BattleRepository battleRepository;

    private Battle battle;

    @BeforeEach
    public void setup() throws IOException {

    }

    @Test
//    @Transactional
    public void test_init_battle() throws InterruptedException {
        UserBase userBaseAttacker = userBaseRepository.findById(250L).get();
        UserBase userBaseDefender = userBaseRepository.findById(251L).get();
        this.battle = initBattleService.initBattle(BattleType.ATTACK, userBaseAttacker, userBaseDefender, null);
        var a = 1L;

    }

    @Test
    @Transactional
    public void test_init_battle_2() throws InterruptedException {
        Battle battle1 = battleRepository.findById(166L).get();
        battlePvPService.startBattle(battle1, null, null);
        List<BattleProfile> battleProfiles = battle1.getBattleProfiles();
        for (BattleProfile battleProfile : battleProfiles) {
            List<ShipLineUp> shipLineUps = battleProfile.getShipLineUps();
            for (ShipLineUp shipLineUp : shipLineUps) {
                var a = shipLineUp.getMotherShip();
                var b = shipLineUp.getEscortShipSquad();
                var c = 1L;
            }
        }
        List<BattleRound> battleRounds = battle1.getBattleRounds();
        BattleRound battleRound = battleRounds.get(0);
        BattleRoundSnapshot snapshot = battleRound.getBattleRoundSnapshot();
        var a = 1L;
    }

}

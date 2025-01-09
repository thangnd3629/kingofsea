package com.supergroup.kos.battle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.supergroup.kos.building.domain.constant.EscortShipType;
import com.supergroup.kos.building.domain.constant.battle.ShipType;
import com.supergroup.kos.building.domain.model.battle.BattleRound;
import com.supergroup.kos.building.domain.model.battle.BattleRoundSnapshot;
import com.supergroup.kos.building.domain.model.battle.BattleUnit;
import com.supergroup.kos.building.domain.model.battle.EscortShipBattle;
import com.supergroup.kos.building.domain.model.battle.MotherShipBattle;
import com.supergroup.kos.building.domain.model.battle.logic.Attack;
import com.supergroup.kos.building.domain.model.battle.logic.AttackDamage;
import com.supergroup.kos.building.domain.repository.persistence.battle.BattleRoundRepository;
import com.supergroup.kos.building.domain.repository.persistence.battle.BattleRoundSnapshotRepository;
import com.supergroup.kos.building.domain.service.battle.BattlePvPService;

@SpringBootTest()
@Transactional
public class BattleLogicTest {
    @Autowired
    private BattlePvPService              battlePvPService;
    @Autowired
    private BattleRoundRepository         battleRoundRepository;
    @Autowired
    private BattleRoundSnapshotRepository battleRoundSnapshotRepository;

    @BeforeEach
    public void setup() throws IOException {
    }

    @Test
    public void test_battle_logic() throws InterruptedException {
        BattleRound battleRound = new BattleRound();
        BattleRoundSnapshot snapshot = new BattleRoundSnapshot();
        Attack attacker = new Attack();
        Attack defender = new Attack();
        Map<String, List<BattleUnit>> mapAttacker = new HashMap<>();
        Map<String, List<BattleUnit>> mapDefender = new HashMap<>();
        // motherShip
        List<BattleUnit> list1 = new ArrayList<>();
        list1.add(new MotherShipBattle().setHpAfterBattle(100L).setMotherShipId(0L).setCurrentHp(100L).setAmount(1).setAtk1(10L).setAtk2(10L)
                                        .setDef1(0D).setDef2(0D));
        mapAttacker.put(battlePvPService.builtKeyShip(ShipType.MOTHER_SHIP, null), list1);

        List<BattleUnit> list2 = new ArrayList<>();
        list2.add(new MotherShipBattle().setHpAfterBattle(100L).setMotherShipId(0L).setCurrentHp(100L).setAmount(1).setAtk1(10L).setAtk2(10L)
                                        .setDef1(0D).setDef2(0D));
        mapDefender.put(battlePvPService.builtKeyShip(ShipType.MOTHER_SHIP, null), list2);


        // escortShip
        createMapShipBattle(mapAttacker);
        createMapShipBattle(mapDefender);
        snapshot.setAttackerModel(attacker.setBattleFields(mapAttacker))
                .setDefenderModel(defender.setBattleFields(mapDefender));

        snapshot = battleRoundSnapshotRepository.save(snapshot);
        battleRound.setBattleRoundSnapshot(snapshot);
        battleRound = battleRoundRepository.save(battleRound);

        battlePvPService.finalRoundBattle(battleRound.getBattle());

//        Assertions.assertEquals();
    }

    @Test
    public void test_taken_damage_1() {
        BattleUnit shipBattle = new MotherShipBattle().setCurrentHp(100L).setDef1(0D).setDef2(0D);
        AttackDamage attackDamage = new AttackDamage().setAtk1(50L).setAtk2(50L);
        battlePvPService.takeDamageToShipBattle(attackDamage, shipBattle);
        Assertions.assertEquals(0, attackDamage.getAtk1());
        Assertions.assertEquals(0,attackDamage.getAtk2());
        Assertions.assertEquals(0,shipBattle.getCurrentHp());
        Assertions.assertEquals(true,shipBattle.getIsTookDamage());
    }

    @Test
    public void test_taken_damage_2() {
        BattleUnit shipBattle = new MotherShipBattle().setCurrentHp(150L).setDef1(0D).setDef2(0D);
        AttackDamage attackDamage = new AttackDamage().setAtk1(50L).setAtk2(50L);
        battlePvPService.takeDamageToShipBattle(attackDamage, shipBattle);
        Assertions.assertEquals(0, attackDamage.getAtk1());
        Assertions.assertEquals(0,attackDamage.getAtk2());
        Assertions.assertEquals(50,shipBattle.getCurrentHp());
    }

    @Test
    public void test_taken_damage_3() {
        BattleUnit shipBattle = new MotherShipBattle().setCurrentHp(50L).setDef1(0D).setDef2(0D);
        AttackDamage attackDamage = new AttackDamage().setAtk1(50L).setAtk2(50L);
        battlePvPService.takeDamageToShipBattle(attackDamage, shipBattle);
        Assertions.assertEquals(25, attackDamage.getAtk1());
        Assertions.assertEquals(25,attackDamage.getAtk2());
        Assertions.assertEquals(0,shipBattle.getCurrentHp());
    }

//    @Test
//    public void test_taken_damage_4() {
//        ShipBattle shipBattle = new MotherShipBattle().setCurrentHp(1L).setDef1(1D).setDef2(1D);
//        AttackDamage attackDamage = new AttackDamage().setAtk1(50L).setAtk2(50L);
//        battleService.takeDamageToShipBattle(attackDamage, shipBattle);
//        Assertions.assertEquals(25,attackDamage.getAtk1());
//        Assertions.assertEquals(25,attackDamage.getAtk2());
//        Assertions.assertEquals(0,shipBattle.getCurrentHp());
//    }

    private void createMapShipBattle(Map<String, List<BattleUnit>> maps) {
        for (EscortShipType escortShipType : EscortShipType.values()) {
            List<BattleUnit> list = new ArrayList<>();
            list.add(new EscortShipBattle().setEscortShipSquadId(0L).setHpLost(0L).setEscortShipType(escortShipType).setHp(20L)
                                           .setDef1(0D).setDef2(0D).setAmount(100).setAtk1(1L).setAtk2(1L));
            maps.put(battlePvPService.builtKeyShip(ShipType.ESCORT_SHIP, escortShipType), list);
        }
    }
}

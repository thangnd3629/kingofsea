package com.supergroup.kos.battle;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.supergroup.kos.KOSApplication;
import com.supergroup.kos.building.domain.command.UserCommand;
import com.supergroup.kos.building.domain.constant.battle.BattleStatus;
import com.supergroup.kos.building.domain.constant.battle.BattleType;
import com.supergroup.kos.building.domain.constant.battle.FactionType;
import com.supergroup.kos.building.domain.model.battle.Battle;
import com.supergroup.kos.building.domain.model.battle.BattleProfile;
import com.supergroup.kos.building.domain.model.battle.BattleReport;
import com.supergroup.kos.building.domain.model.config.QueenConfig;
import com.supergroup.kos.building.domain.model.config.RelicConfig;
import com.supergroup.kos.building.domain.model.config.WeaponConfig;
import com.supergroup.kos.building.domain.model.profile.KosProfile;
import com.supergroup.kos.building.domain.model.queen.Queen;
import com.supergroup.kos.building.domain.model.relic.Relic;
import com.supergroup.kos.building.domain.model.weapon.Weapon;
import com.supergroup.kos.building.domain.repository.persistence.battle.BattleRepository;
import com.supergroup.kos.building.domain.service.asset.AssetsService;
import com.supergroup.kos.building.domain.service.battle.BattlePvPService;
import com.supergroup.kos.building.domain.service.building.CastleBuildingService;
import com.supergroup.kos.building.domain.service.profile.KosProfileService;
import com.supergroup.kos.building.domain.service.queen.QueenService;
import com.supergroup.kos.building.domain.service.relic.RelicService;
import com.supergroup.kos.building.domain.service.weapon.WeaponService;

@SpringBootTest(classes = KOSApplication.class, properties = { "seamap.parcel-size=40", "seamap.size=2000" })
@Transactional
@Rollback
@ActiveProfiles("test")
public class BattleRewardTest {

    @Autowired
    private KosProfileService kosProfileService;
    @Autowired
    private BattlePvPService  battlePvPService;
    @Autowired
    private BattleRepository  battleRepository;
    @Autowired
    private AssetsService     assetsService;
    @Autowired
    private RelicService      relicService;
    @Autowired
    private QueenService      queenService;
    @Autowired
    private WeaponService     weaponService;

    private Battle                battle;
    private KosProfile            attackerKosProfile;
    private KosProfile            defenderKosProfile;
    @Autowired
    private CastleBuildingService castleBuildingService;

    @BeforeEach
    public void setup() {
        attackerKosProfile = kosProfileService.getKosProfile(new UserCommand().setUserId(1L));
        defenderKosProfile = kosProfileService.getKosProfile(new UserCommand().setUserId(2L));

        attackerKosProfile.setUser(null);
        attackerKosProfile.setUser(null);

        kosProfileService.saveProfile(attackerKosProfile);
        kosProfileService.saveProfile(defenderKosProfile);

        attackerKosProfile = kosProfileService.createNewProfile(new UserCommand().setUserId(1L));
        defenderKosProfile = kosProfileService.createNewProfile(new UserCommand().setUserId(2L));

        //add resource
        defenderKosProfile.getAssets()
                          .setWood(1000.0)
                          .setStone(2000.0)
                          .setGold(3000.0);
        for (int i = 0; i < 10; i++) {
            var relic = new Relic();
            relic.setRelicConfig(new RelicConfig().setId(1L))
                 .setCommunityBuilding(defenderKosProfile.getCommunityBuilding());
            relicService.save(relic);
        }
        for (int i = 0; i < 10; i++) {
            var queen = new Queen();
            queen.setQueenConfig(new QueenConfig().setId(1L))
                 .setQueenBuilding(defenderKosProfile.getQueenBuilding());
            queenService.save(queen);
        }

        for (int i = 0; i < 10; i++) {
            var weapon = new Weapon();
            weapon.setWeaponConfig(new WeaponConfig().setId(1L))
                  .setAssets(defenderKosProfile.getAssets());
            weaponService.save(weapon);
        }

        assetsService.save(defenderKosProfile.getAssets());

        var attacker = new BattleProfile().setKosProfile(attackerKosProfile);
        var defender = new BattleProfile().setKosProfile(defenderKosProfile);

        battle = new Battle();
        battle.setBattleReport(new BattleReport());
        battle.setAttacker(attacker);
        battle.setDefender(defender);
        battle.setBattleType(BattleType.ATTACK);
        battle.setStatus(BattleStatus.END);
        battle.setWinner(attacker);

        attacker.setBattle(battle);
        defender.setBattle(battle);

        battle = battleRepository.save(battle);
    }

    @Test
    public void test_claim_battle_reward_layer_one() {
        battle.getAttacker().getKosProfile().getCastleBuilding().setLevel(1L);
        battle.getDefender().getKosProfile().getCastleBuilding().setLevel(11L);
        castleBuildingService.save(battle.getAttacker().getKosProfile().getCastleBuilding());
        castleBuildingService.save(battle.getDefender().getKosProfile().getCastleBuilding());
        battlePvPService.onBattleEnded(battle, FactionType.ATTACKER);
    }
}

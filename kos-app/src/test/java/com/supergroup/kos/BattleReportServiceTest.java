package com.supergroup.kos;

import java.time.LocalDateTime;
import java.util.List;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;

import com.supergroup.kos.building.domain.command.UserCommand;
import com.supergroup.kos.building.domain.constant.battle.BattleStatus;
import com.supergroup.kos.building.domain.constant.battle.BattleType;
import com.supergroup.kos.building.domain.constant.battle.FactionType;
import com.supergroup.kos.building.domain.dto.battle.GetBattleByPageCommand;
import com.supergroup.kos.building.domain.model.battle.Battle;
import com.supergroup.kos.building.domain.model.battle.BattleProfile;
import com.supergroup.kos.building.domain.model.battle.BattleReport;
import com.supergroup.kos.building.domain.model.battle.BattleRound;
import com.supergroup.kos.building.domain.model.battle.DamageReport;
import com.supergroup.kos.building.domain.model.battle.MotherShipReport;
import com.supergroup.kos.building.domain.model.battle.RoundReport;
import com.supergroup.kos.building.domain.model.profile.KosProfile;
import com.supergroup.kos.building.domain.repository.persistence.battle.BattleProfileRepository;
import com.supergroup.kos.building.domain.repository.persistence.battle.BattleReportRepository;
import com.supergroup.kos.building.domain.repository.persistence.battle.BattleRepository;
import com.supergroup.kos.building.domain.service.battle.BattleReportService;
import com.supergroup.kos.building.domain.service.profile.KosProfileService;

@SpringBootTest(classes = KOSApplication.class, properties = { "seamap.parcel-size=40", "seamap.size=2000" })
@Transactional
@Rollback
@ActiveProfiles("test")
public class BattleReportServiceTest {

    @Autowired
    private BattleReportService    battleReportService;
    @Autowired
    private BattleReportRepository battleReportRepository;
    @Autowired
    private BattleRepository       battleRepository;
    @Autowired
    private KosProfileService      kosProfileService;

    @Autowired
    private BattleProfileRepository battleProfileRepository;

    private KosProfile   kosProfile;
    private Battle       battle;
    private BattleReport battleReport;
    private RoundReport  roundReport;

    @BeforeEach
    public void setup() {
        kosProfile = kosProfileService.createNewProfile(new UserCommand().setUserId(1L));
        battle = new Battle();
        var rounds = List.of(new BattleRound().setBattle(battle), new BattleRound().setBattle(battle));
        battle.setStatus(BattleStatus.PROGRESS)
              .setBattleType(BattleType.ATTACK)
              .setBattleRounds(rounds);
        battleRepository.save(battle);
        battleReport = new BattleReport();
        battleReport.setBattle(battle);

        new BattleProfile().setBattle(battle)
                           .setBattleReport(battleReport)
                           .setUsername("Test user 1")
                           .setKosProfile(kosProfile);

        var joiner = List.of(new BattleProfile().setBattle(battle)
                                                .setBattleReport(battleReport)
                                                .setUsername("Test user 1")
                                                .setKosProfile(kosProfile),
                             new BattleProfile().setBattle(battle)
                                                .setBattleReport(battleReport)
                                                .setUsername("Test user 2")
                                                .setKosProfile(kosProfile));
        joiner = battleProfileRepository.saveAll(joiner);
        battleReport.setJoiners(joiner);

        battleReport.setInitiator(joiner.get(0));
        battleReport.setVictim(joiner.get(1));
        battleReport.setStartAt(LocalDateTime.now());

        roundReport = new RoundReport().setBattleReport(battleReport)
                                       .setRound(rounds.get(0))
                                       .setAttackerDamageReport(new DamageReport().setArmour(0L))
                                       .setDefenderDamageReport(new DamageReport().setArmour(0L))
                                       .setAttackerMotherShipReports(List.of(new MotherShipReport(FactionType.ATTACKER).setCurrentHp(10L)))
                                       .setDefenderMotherShipReports(List.of(new MotherShipReport(FactionType.DEFENDER).setCurrentHp(10L)));
        battleReport.setRoundReports(List.of(roundReport));
        battleReport = battleReportRepository.save(battleReport);
        roundReport = battleReport.getRoundReports().get(0);
    }

    @Test
    public void test_get_round_report() {
        var battleReports = battleReportService.getBattleReportByPage(
                new GetBattleByPageCommand(kosProfile, PageRequest.of(0, 10, Sort.by("createdAt"))));
        Assertions.assertFalse(battleReports.isEmpty());
    }

    @Test
    public void test_get_round_report_by_id() {
        var report = battleReportService.getRoundReportById(roundReport.getId(), battle.getId(), kosProfile.getId());
        Assertions.assertNotNull(report);
    }
}

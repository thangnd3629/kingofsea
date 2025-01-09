package com.supergroup.kos;

import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.supergroup.asset.service.AssetService;
import com.supergroup.kos.building.domain.repository.persistence.battle.BattleReportRepository;
import com.supergroup.kos.building.domain.repository.persistence.battle.BattleRoundRepository;
import com.supergroup.kos.building.domain.repository.persistence.profile.KosProfileRepository;
import com.supergroup.kos.building.domain.repository.persistence.seamap.SeaElementConfigRepository;
import com.supergroup.kos.mapper.battle.BattleReportMapper;

@SpringBootTest(classes = KOSApplication.class)
@ActiveProfiles("local")
@Transactional
public class BattleReportTest {

    @Autowired
    private BattleReportRepository     battleReportRepository;
    @Autowired
    private BattleReportMapper         battleReportMapper;
    @Autowired
    private AssetService               assetService;
    @Autowired
    private SeaElementConfigRepository seaElementConfigRepository;
    @Autowired
    private KosProfileRepository       kosProfileRepository;
    @Autowired
    private BattleRoundRepository      battleRoundRepository;

    @Test
    public void testGetBattleReportByIdNativeQuery() {
        var battleReport = battleReportRepository.findBattleReportByKosProfileId(329L);
        var kosProfile = kosProfileRepository.findById(329L).orElseThrow();
        Assertions.assertFalse(battleReport.isEmpty());
        var responses = battleReport.stream()
                                    .map(e -> battleReportMapper.map(e,
                                                                     kosProfile,
                                                                     assetService,
                                                                     battleRoundRepository,
                                                                     seaElementConfigRepository))
                                    .collect(Collectors.toList());
        Assertions.assertFalse(responses.isEmpty());
    }
}

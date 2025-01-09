package com.supergroup.kos;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.supergroup.kos.building.domain.command.InitSeaActivityCommand;
import com.supergroup.kos.building.domain.command.PrepareShipLineupCommand;
import com.supergroup.kos.building.domain.constant.EscortShipType;
import com.supergroup.kos.building.domain.dto.seamap.EscortSquadDTO;
import com.supergroup.kos.building.domain.model.seamap.ShipLineUp;
import com.supergroup.kos.building.domain.model.seamap.movesession.MissionType;
import com.supergroup.kos.building.domain.service.profile.KosProfileService;
import com.supergroup.kos.building.domain.service.seamap.activity.LineUpService;
import com.supergroup.kos.building.domain.service.seamap.activity.SeaActivityService;

@SpringBootTest
public class MiningTest {
    @Autowired
    private SeaActivityService seaActivityService;
    @Autowired
    private LineUpService lineUpService;
    @Autowired
    private KosProfileService kosProfileService;


        @Test
    public void startOccupy() {
        List<EscortSquadDTO> squadHuyenStp = new ArrayList<>();
        squadHuyenStp.add(new EscortSquadDTO().setEscortShipType(EscortShipType.RAMMER_SHIP).setAmount(0L));
        ShipLineUp lineUpHuyenStp = lineUpService.updateLineUp(
                new PrepareShipLineupCommand().setKosProfileId(95L).setMotherShipId(330L).setEscortShips(squadHuyenStp));

        seaActivityService.initActivity(
                kosProfileService.getKosProfileById(95L),
                new InitSeaActivityCommand().setKosProfileId(95L).setDestinationId(1013571L)
                                            .setMissionType(MissionType.MINING)
                                            .setLineUpId(lineUpHuyenStp.getId()));

    }
//    @Test
//    public void startBattle1() throws Exception {
//        List<EscortSquadDTO> squadThangStp = new ArrayList<>();
//
//        ShipLineUp lineUpThangStp = lineUpService.updateLineUp(
//                new PrepareShipLineupCommand().setKosProfileId(266L).setMotherShipId(392L).setEscortShips(squadThangStp));
//
//        seaActivityService.initActivity(kosProfileService.getKosProfileById(266L), new InitSeaActivityCommand().setKosProfileId(266L)
//                .setDestinationId(4105L)
//                .setMissionType(MissionType.ATTACK)
//                .setLineUpId(lineUpThangStp.getId()));
//
//    }

//    @Test
//    public void startBattle2() throws Exception {
//        List<EscortSquadDTO> squadThangStp = new ArrayList<>();
//
//        ShipLineUp lineUpThangStp = lineUpService.updateLineUp(
//                new PrepareShipLineupCommand().setKosProfileId(95L).setMotherShipId(330L).setEscortShips(squadThangStp));
//
//        seaActivityService.initActivity(kosProfileService.getKosProfileById(95L), new InitSeaActivityCommand().setKosProfileId(95L)
//                .setDestinationId(4105L)
//                .setMissionType(MissionType.OCCUPY)
//                .setLineUpId(lineUpThangStp.getId()));
//
//    }
//    @Test
//    public void stationOnBase() throws InterruptedException {
//        List<EscortSquadDTO> squadHuyenStp = new ArrayList<>();
//
//        squadHuyenStp.add(new EscortSquadDTO().setEscortShipType(EscortShipType.RAMMER_SHIP).setAmount(5L));
//        ShipLineUp lineUpHuyenStp = lineUpService.updateLineUp(
//                new PrepareShipLineupCommand().setKosProfileId(123L).setMotherShipId(135L).setEscortShips(squadHuyenStp));
//
//        seaActivityService.initActivity(
//                kosProfileService.getKosProfileById(123L),
//                new InitSeaActivityCommand().setKosProfileId(123L).setDestinationId(219L)
//                                            .setMissionType(MissionType.STATION)
//                                            .setLineUpId(lineUpHuyenStp.getId()));
//
//    }
//    @Test
//    public void startBattle() throws Exception{
//        List<EscortSquadDTO> squadThangStp = new ArrayList<>();
//        squadThangStp.add(new EscortSquadDTO().setEscortShipType(EscortShipType.RAMMER_SHIP).setAmount(5L));
//        squadThangStp.add(new EscortSquadDTO().setEscortShipType(EscortShipType.CHARGER_SHIP).setAmount(5L));
//        ShipLineUp lineUpThangStp = lineUpService.updateLineUp(
//                new PrepareShipLineupCommand().setKosProfileId(31L).setMotherShipId(6L).setEscortShips(squadThangStp));
//
//        seaActivityService.initActivity(kosProfileService.getKosProfileById(31L), new InitSeaActivityCommand().setKosProfileId(31L)
//                                                                                                              .setDestinationId(219L)
//                                                                                                              .setMissionType(MissionType.OCCUPY)
//                                                                                                              .setLineUpId(lineUpThangStp.getId()));
//
//    }
//    @Test
//    public void startMiningMission() {
//        List<EscortSquadDTO> squadHuyenStp = new ArrayList<>();
//        squadHuyenStp.add(new EscortSquadDTO().setEscortShipType(EscortShipType.RAMMER_SHIP).setAmount(5L));
//        ShipLineUp lineUpHuyenStp = lineUpService.updateLineUp(
//                new PrepareShipLineupCommand().setKosProfileId(95L).setMotherShipId(120L).setEscortShips(squadHuyenStp));
//
//        seaActivityService.initActivity(
//                kosProfileService.getKosProfileById(95L),
//                new InitSeaActivityCommand().setKosProfileId(95L).setDestinationId(217167L)
//                                            .setMissionType(MissionType.ATTACK)
//                                            .setLineUpId(lineUpHuyenStp.getId()));
//
//    }

}

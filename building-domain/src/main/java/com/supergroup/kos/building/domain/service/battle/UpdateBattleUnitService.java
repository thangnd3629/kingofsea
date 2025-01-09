package com.supergroup.kos.building.domain.service.battle;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.supergroup.core.constant.ErrorCode;
import com.supergroup.core.exception.KOSException;
import com.supergroup.kos.building.domain.command.PrepareShipLineupCommand;
import com.supergroup.kos.building.domain.model.battle.BattleProfile;
import com.supergroup.kos.building.domain.model.seamap.EscortShipSquad;
import com.supergroup.kos.building.domain.model.seamap.ShipLineUp;
import com.supergroup.kos.building.domain.model.ship.EscortShip;
import com.supergroup.kos.building.domain.model.ship.MotherShip;
import com.supergroup.kos.building.domain.repository.persistence.ship.MotherShipRepository;
import com.supergroup.kos.building.domain.service.seamap.EscortShipSquadService;
import com.supergroup.kos.building.domain.service.seamap.activity.LineUpService;

@Service
public class UpdateBattleUnitService {
    private EscortShipSquadService escortShipSquadService;
    private LineUpService          lineUpService;
    private MotherShipRepository   motherShipRepository;

    public UpdateBattleUnitService(@Lazy LineUpService lineUpService,
                                   EscortShipSquadService escortShipSquadService,
                                   MotherShipRepository motherShipRepository) {
        this.escortShipSquadService = escortShipSquadService;
        this.lineUpService = lineUpService;
        this.motherShipRepository = motherShipRepository;
    }

    public void addEscortShipForBattleWhenCompleteBuild(BattleProfile battleProfile, EscortShip escortShip, Long amount) {
        ShipLineUp shipLineUp = null;
        List<ShipLineUp> shipLineUps = lineUpService.findByBattleProfileIdAndTimeJoinedBattleAfter(battleProfile.getId(),
                                                                                                   battleProfile.getBattle().getTimeUpdateStatus());
        if (shipLineUps.isEmpty()) {
            shipLineUp = lineUpService.save(new ShipLineUp().setBattleProfile(battleProfile).setTimeJoinedBattle(LocalDateTime.now()));
        } else {
            shipLineUp = battleProfile.getShipLineUps().get(0);
        }

        Optional<EscortShipSquad> optionalEscortShipSquad = escortShipSquadService.findByEscortShipIdAndLineUp(escortShip.getId(),
                                                                                                               shipLineUp.getId());
        EscortShipSquad escortShipSquad = null;
        if (optionalEscortShipSquad.isEmpty()) {
            escortShipSquad = new EscortShipSquad().setEscortShip(escortShip).setLineUp(shipLineUp).setAmount(amount);
        } else {
            escortShipSquad = optionalEscortShipSquad.get();
            escortShipSquad.setAmount(escortShipSquad.getAmount() + amount);
        }
        escortShipSquadService.save(escortShipSquad);
    }

    public void addMotherShipForBattleWhenBuy(BattleProfile battleProfile, Long motherShipId) {
        MotherShip motherShip = motherShipRepository.findById(motherShipId).orElseThrow(() -> KOSException.of(ErrorCode.MOTHER_SHIP_IS_NOT_FOUND));
        ShipLineUp shipLineUp = lineUpService.updateLineUp(new PrepareShipLineupCommand().setEscortShips(new ArrayList<>())
                                                                                         .setKosProfileId(battleProfile.getKosProfile().getId())
                                                                                         .setMotherShipId(motherShipId));
        shipLineUp.setBattleProfile(battleProfile)
                  .setTimeJoinedBattle(LocalDateTime.now())
                  .setMotherShip(motherShip);
        lineUpService.save(shipLineUp);
    }
}

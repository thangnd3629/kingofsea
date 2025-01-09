package com.supergroup.admin.api;

import java.time.LocalDateTime;
import java.util.Objects;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.supergroup.admin.dto.ShipLoad;
import com.supergroup.core.constant.ErrorCode;
import com.supergroup.core.exception.KOSException;
import com.supergroup.kos.building.domain.constant.seamap.SeaActivityStatus;
import com.supergroup.kos.building.domain.model.seamap.SeaActivity;
import com.supergroup.kos.building.domain.model.seamap.reward.LoadedOnShipReward;
import com.supergroup.kos.building.domain.model.ship.MotherShip;
import com.supergroup.kos.building.domain.repository.persistence.ship.MotherShipRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/v1/admin/mother-ship")
@RequiredArgsConstructor
@Slf4j
public class MotherShipRestController {
    private final MotherShipRepository motherShipRepository;

    @GetMapping("{id}/load")
    public ResponseEntity<?> getLoad(@PathVariable Long id) {
        MotherShip motherShip = motherShipRepository.findById(id).orElseThrow(()-> KOSException.of(ErrorCode.MOTHER_SHIP_IS_NOT_FOUND));
        ShipLoad shipLoad = new ShipLoad();
        if (Objects.nonNull(motherShip.getActiveLineUp())) {
            SeaActivity activity = motherShip.getActiveLineUp().getActivity();
            LoadedOnShipReward loaded = activity.getLoadedOnShipReward();
            shipLoad.setGold(loaded.getGold());
            shipLoad.setWood(loaded.getWood());
            shipLoad.setStone(loaded.getStone());
        }
        return ResponseEntity.ok(shipLoad);
    }

    @PostMapping("{id}/reset-hp")
    public ResponseEntity<?> recoveryHp(@PathVariable Long id) {
        MotherShip motherShip = motherShipRepository.findById(id).orElseThrow(() -> KOSException.of(ErrorCode.MOTHER_SHIP_IS_NOT_FOUND));
        // check status mother ship
        if (!motherShip.getStatus().equals(SeaActivityStatus.STANDBY)) {
            return ResponseEntity.badRequest().body("Mother ship must be stand by");
        }
        // set current hp is 0 for mother ship, restart healing time
        motherShip.setCurrentHp(0L);
        motherShip.setArrivalMainBaseTime(LocalDateTime.now());
        motherShip.setLastTimeCalculateHp(null);
        motherShipRepository.save(motherShip);
        return ResponseEntity.accepted().build();
    }

}

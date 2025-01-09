package com.supergroup.admin.api;

import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.supergroup.kos.building.domain.model.mining.PeopleAndGoldMiningSnapshot;
import com.supergroup.kos.building.domain.service.building.CastleBuildingService;
import com.supergroup.kos.building.domain.service.mining.PeopleAndGoldMiningService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/admin/public/test")
@RequiredArgsConstructor
public class TestMiningController {
    private final CastleBuildingService      castleBuildingService;
    private final PeopleAndGoldMiningService peopleAndGoldMiningService;

    @PostMapping("/mining-people")
    public ResponseEntity test(@RequestBody PeopleAndGoldMiningSnapshot snapshot, @RequestParam(name = "time") Long time) {
        snapshot.setLastTimeClaim(LocalDateTime.now().minusSeconds(time));
        return ResponseEntity.ok(peopleAndGoldMiningService.getMiningClaim(snapshot));
    }
 }

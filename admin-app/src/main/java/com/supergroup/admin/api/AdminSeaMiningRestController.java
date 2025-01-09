package com.supergroup.admin.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.supergroup.admin.domain.service.AdminMiningService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/admin/seamap/mine")
public class AdminSeaMiningRestController {
    private final AdminMiningService adminMiningService;

    @PutMapping("/{id}")
    public ResponseEntity<?> resetMine(@PathVariable Long id) {
        adminMiningService.resetMine(id);
        return ResponseEntity.ok("OK");
    }
    @PutMapping("/cap/{id}")
    public ResponseEntity<?> changeCap(@PathVariable Long id, @RequestParam(name = "capacity") Double capacity) {
        adminMiningService.changeMaxCap(id, capacity);
        return ResponseEntity.ok("OK");
    }
}

package com.supergroup.admin.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.supergroup.admin.mapper.LineUpMapper;
import com.supergroup.core.constant.ErrorCode;
import com.supergroup.core.exception.KOSException;
import com.supergroup.kos.building.domain.model.seamap.ShipLineUp;
import com.supergroup.kos.building.domain.repository.persistence.seamap.LineUpRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/admin/lineup")
public class AdminLineUpRestController {
    private final LineUpMapper lineUpMapper;
    private final LineUpRepository lineUpRepository;
    @GetMapping("/{id}")
    public ResponseEntity<?> getLineUpById(@PathVariable Long id){
        ShipLineUp lineUp = lineUpRepository.findById(id).orElseThrow(()-> KOSException.of(ErrorCode.SHIP_LINE_UP_NOT_FOUND));
        return ResponseEntity.ok(
                lineUpMapper.toDto(lineUp)
                                );
    }
}

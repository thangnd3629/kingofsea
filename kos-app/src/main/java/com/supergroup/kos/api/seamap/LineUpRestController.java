package com.supergroup.kos.api.seamap;

import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.supergroup.kos.building.domain.command.PrepareShipLineupCommand;
import com.supergroup.kos.building.domain.command.UserCommand;
import com.supergroup.kos.building.domain.model.profile.KosProfile;
import com.supergroup.kos.building.domain.model.seamap.ShipLineUp;
import com.supergroup.kos.building.domain.service.profile.KosProfileService;
import com.supergroup.kos.building.domain.service.seamap.activity.LineUpService;
import com.supergroup.kos.dto.PageResponse;
import com.supergroup.kos.dto.seamap.activity.PrepareLineUpRequest;
import com.supergroup.kos.dto.seamap.activity.ShipLineUpDTO;
import com.supergroup.kos.mapper.seamap.activity.LineUpMapper;
import com.supergroup.kos.util.AuthUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@RequestMapping("/v1/seamap/lineup")
public class LineUpRestController {
    private final LineUpService lineUpService;
    private final KosProfileService kosProfileService;
    private final LineUpMapper  lineUpMapper;

    @PostMapping("")
    public ResponseEntity<ShipLineUpDTO> updateLineUp(@RequestBody @Valid PrepareLineUpRequest request){
        KosProfile kosProfile = kosProfileService.getKosProfile(new UserCommand().setUserId(AuthUtil.getUserId()));
        ShipLineUp lineUp = lineUpService.updateLineUp(new PrepareShipLineupCommand()
                                                               .setKosProfileId(kosProfile.getId())
                                                               .setEscortShips(request.getLineUp().getEscortShips())
                                                               .setMotherShipId(request.getMotherShipId())
                                                      );
        return ResponseEntity.ok(lineUpMapper.toDto(lineUp));
    }
    @GetMapping("/")
    public ResponseEntity<PageResponse<ShipLineUpDTO>> getLineUps(@RequestParam(name = "mothershipId") Long motherShipId, Pageable pageable){
        KosProfile kosProfile = kosProfileService.getKosProfile(new UserCommand().setUserId(AuthUtil.getUserId()));
        Page<ShipLineUp> lineUps = lineUpService.getLatestLineUp(kosProfile.getId(), motherShipId, pageable);
        PageResponse<ShipLineUpDTO> page = new PageResponse<>();
        page.setData(lineUpMapper.toDtos(lineUps.toList()));
        page.setTotal(lineUps.getTotalElements());
        return ResponseEntity.ok(page);
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getLineUp(@PathVariable(name = "id") Long lineUpId)
    {
        KosProfile kosProfile = kosProfileService.getKosProfile(new UserCommand().setUserId(AuthUtil.getUserId()));
        ShipLineUp lineUp = lineUpService.getLineUpById(kosProfile.getId(), lineUpId);
        return ResponseEntity.ok(lineUpMapper.toDto(lineUp));
    }
}

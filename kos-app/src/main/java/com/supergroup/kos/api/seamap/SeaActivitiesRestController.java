package com.supergroup.kos.api.seamap;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.supergroup.core.constant.ErrorCode;
import com.supergroup.core.exception.KOSException;
import com.supergroup.kos.building.domain.command.InitSeaActivityCommand;
import com.supergroup.kos.building.domain.command.UserCommand;
import com.supergroup.kos.building.domain.command.WithdrawActivityCommand;
import com.supergroup.kos.building.domain.model.profile.KosProfile;
import com.supergroup.kos.building.domain.model.seamap.SeaActivity;
import com.supergroup.kos.building.domain.model.seamap.SeaElement;
import com.supergroup.kos.building.domain.model.seamap.movesession.MissionType;
import com.supergroup.kos.building.domain.service.profile.KosProfileService;
import com.supergroup.kos.building.domain.service.seamap.SeaElementService;
import com.supergroup.kos.building.domain.service.seamap.activity.LineUpService;
import com.supergroup.kos.building.domain.service.seamap.activity.SeaActivityService;
import com.supergroup.kos.dto.seamap.activity.DeployTroopRequest;
import com.supergroup.kos.dto.seamap.activity.SeaActivityDTO;
import com.supergroup.kos.dto.seamap.activity.ShipLineUpDTO;
import com.supergroup.kos.mapper.seamap.activity.LineUpMapper;
import com.supergroup.kos.mapper.seamap.activity.SeaActivityMapper;
import com.supergroup.kos.util.AuthUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@RequestMapping("/v1/seamap/activity")
public class SeaActivitiesRestController {
    private final SeaActivityService seaActivityService;
    private final KosProfileService  kosProfileService;
    private final SeaActivityMapper  seaActivityMapper;
    private final LineUpMapper       lineUpMapper;
    private final SeaElementService  seaElementService;
    private final LineUpService      lineUpService;

    @PostMapping("")
    public ResponseEntity<SeaActivityDTO> moveOnSea(@RequestBody @Valid DeployTroopRequest request) {
        KosProfile kosProfile = kosProfileService.getKosProfile(new UserCommand().setUserId(AuthUtil.getUserId()));
        InitSeaActivityCommand command = new InitSeaActivityCommand()
                .setDestinationId(request.getDestinationId())
                .setLineUpId(request.getLineUpId())
                .setMissionType(request.getMissionType())
                .setKosProfileId(kosProfile.getId());
        if (Objects.isNull(request.getMissionType())) {
            command.setMissionType(MissionType.ATTACK);
        }
        SeaActivity activity = seaActivityService.initActivity(kosProfile, command);
        SeaActivityDTO activityDTO = seaActivityMapper.toDto(activity, lineUpMapper, seaElementService, lineUpService);
        return ResponseEntity.ok(activityDTO);
    }

    @GetMapping("")
    public ResponseEntity<List<SeaActivityDTO>> getActiveActivities() {
        KosProfile kosProfile = kosProfileService.getKosProfile(new UserCommand().setUserId(AuthUtil.getUserId()));
        List<SeaActivity> activityList;
        activityList = seaActivityService.findActiveActivity(kosProfile.getId());
        List<SeaActivityDTO> results = seaActivityMapper.toDtos(activityList, lineUpMapper, seaElementService, lineUpService);
        for (SeaActivityDTO result : results) {
            ShipLineUpDTO lineUp = result.getShipLineUp();
            if (Objects.nonNull(lineUp) && Objects.nonNull(lineUp.getMotherShip())) {
                // if mother ship on duty, current location is activity's location
                SeaElement seaElement = seaActivityService.findElementToReturn(result.getId());
                if (Objects.isNull(seaElement)) {continue;}
                lineUp.getMotherShip().setReturnLocation(seaElement.getCoordinates());
            }
        }
        return ResponseEntity.ok(results);
    }

    @PostMapping("/withdraw")
    public ResponseEntity<?> withDraw(@RequestParam(name = "activityIds") List<Long> activitiesIds) {
        List<SeaActivityDTO> withdrawAbles = new ArrayList<>();
        for (Long id : activitiesIds) {
            try {
                SeaActivity activity = seaActivityService.withdraw(new WithdrawActivityCommand().setId(id));
                withdrawAbles.add(seaActivityMapper.toDto(activity, lineUpMapper, seaElementService, lineUpService));
            } catch (KOSException e) {
                if (e.getCode().equals(ErrorCode.CAN_NOT_WITHDRAW)) {} else {
                    throw e;
                }
            }
        }
        return ResponseEntity.ok(withdrawAbles);
    }

}

package com.supergroup.kos.api.scout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.supergroup.asset.service.AssetService;
import com.supergroup.auth.domain.model.UserProfile;
import com.supergroup.auth.domain.service.UserProfileService;
import com.supergroup.kos.building.domain.command.ActivityScoutCommand;
import com.supergroup.kos.building.domain.command.DeleteScoutReportCommand;
import com.supergroup.kos.building.domain.command.GetScoutReportCommand;
import com.supergroup.kos.building.domain.command.UpdateScoutReportCommand;
import com.supergroup.kos.building.domain.command.UserCommand;
import com.supergroup.kos.building.domain.constant.MissionResult;
import com.supergroup.kos.building.domain.constant.TypeUpdateScoutReport;
import com.supergroup.kos.building.domain.model.config.QueenConfig;
import com.supergroup.kos.building.domain.model.config.RelicConfig;
import com.supergroup.kos.building.domain.model.scout.Location;
import com.supergroup.kos.building.domain.model.scout.ScoutReport;
import com.supergroup.kos.building.domain.model.scout.ScoutingResult;
import com.supergroup.kos.building.domain.model.scout.ShipScoutingResult;
import com.supergroup.kos.building.domain.model.seamap.Coordinates;
import com.supergroup.kos.building.domain.repository.persistence.queen.QueenConfigDataSource;
import com.supergroup.kos.building.domain.repository.persistence.relic.RelicConfigDataSource;
import com.supergroup.kos.building.domain.service.profile.KosProfileService;
import com.supergroup.kos.building.domain.service.scout.ScoutReportService;
import com.supergroup.kos.building.domain.service.scout.ScoutService;
import com.supergroup.kos.building.domain.service.seamap.UserBaseService;
import com.supergroup.kos.dto.PageResponse;
import com.supergroup.kos.dto.queen.QueenConfigResponse;
import com.supergroup.kos.dto.relic.RelicConfigResponse;
import com.supergroup.kos.dto.scout.ActivityScoutRequest;
import com.supergroup.kos.dto.scout.DeleteScoutReportRequest;
import com.supergroup.kos.dto.scout.ScoutReportResponse;
import com.supergroup.kos.dto.scout.ScoutReportResponseDetail;
import com.supergroup.kos.dto.scout.ScoutingResultResponse;
import com.supergroup.kos.mapper.ScoutReportResponseMapper;
import com.supergroup.kos.util.AuthUtil;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/scout")
@RequiredArgsConstructor
public class ScoutRestController {
    private final KosProfileService         kosProfileService;
    private final ScoutService              scoutService;
    private final ScoutReportService        scoutReportService;
    private final UserProfileService        userProfileService;
    private final QueenConfigDataSource     queenConfigDataSource;
    private final RelicConfigDataSource     relicConfigDataSource;
    private final AssetService              assetService;
    private final ScoutReportResponseMapper scoutReportMapper;

    @PostMapping
    public ResponseEntity<?> scoutActivity(@RequestBody @Valid ActivityScoutRequest request) {
        var kosProfile = kosProfileService.getKosProfile(new UserCommand().setUserId(AuthUtil.getUserId()));
        scoutService.activityScout(new ActivityScoutCommand().setKosProfile(kosProfile)
                                                             .setNumberArmy(request.getNumberArmy())
                                                             .setType(request.getType())
                                                             .setCoordinates(new Coordinates().setX(request.getLocation().getX().longValue())
                                                                                              .setY(request.getLocation().getY().longValue())));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/reports")
    public ResponseEntity<?> getAllScoutReport(HttpServletRequest request) {
        Pageable pageable = (Pageable) request.getAttribute("pageable");
        var kosProfile = kosProfileService.getKosProfile(new UserCommand().setUserId(AuthUtil.getUserId()));
        var scoutReports = scoutReportService.getAllScoutReport(kosProfile.getId(), pageable);
        List<ScoutReportResponse> responseList = scoutReportMapper.toResponses(scoutReports);
        return ResponseEntity.ok(new PageResponse<ScoutReportResponse>().setTotal(scoutReports.getTotalElements()).setData(responseList));
    }

    @GetMapping("/reports/{id}")
    public ResponseEntity<?> getScoutReport(@PathVariable Long id) {
        var kosProfile = kosProfileService.getKosProfile(new UserCommand().setUserId(AuthUtil.getUserId()));
        ScoutReport scoutReport = scoutReportService.getScoutReport(new GetScoutReportCommand().setKosProfileId(kosProfile.getId()).setReportId(id));
        return ResponseEntity.ok(mapToScoutReportResponse(scoutReport));
    }

    @PutMapping("/reports/{id}")
    public ResponseEntity<?> updateReport(@PathVariable Long id, @RequestParam(name = "type") TypeUpdateScoutReport type) {
        var kosProfile = kosProfileService.getKosProfile(new UserCommand().setUserId(AuthUtil.getUserId()));
        scoutReportService.updateScoutReport(new UpdateScoutReportCommand().setType(type)
                                                                     .setScoutReportId(id)
                                                                     .setKosProfileId(kosProfile.getId()));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/reports")
    public ResponseEntity<?> getScoutReport(@RequestBody DeleteScoutReportRequest request) {
        var kosProfile = kosProfileService.getKosProfile(new UserCommand().setUserId(AuthUtil.getUserId()));
        scoutReportService.deleteReports(new DeleteScoutReportCommand().setIds(request.getIds()).setKosProfileId(kosProfile.getId()));
        return ResponseEntity.ok().build();
    }

    private ScoutReportResponseDetail mapToScoutReportResponse(ScoutReport scoutReport) {
//        UserProfile userProfile = userProfileService.findByUserId(scoutReport.getKosProfileTarget().getUser().getId());
        ScoutReportResponseDetail scoutReportResponseDetail = new ScoutReportResponseDetail();
        scoutReportResponseDetail.setId(scoutReport.getId())
                                 .setMissionTime(scoutReport.getMissionTime())
                                 .setTimeDone(scoutReport.getTimeStart().plusSeconds(scoutReport.getMissionTime()));
//                                 .setUsername(userProfile.getUsername()) // in detail
//                                 .setAvatarUrl(assetService.getUrl(userProfile.getAvatarUrl()));
//        scoutReportResponseDetail.setLocation(new Location().setX(scoutReport.getInfoBaseUserTarget().getCoordinates().getX())
//                                                            .setY(scoutReport.getInfoBaseUserTarget().getCoordinates().getY()));
        ScoutingResultResponse scoutingResultResponse = new ScoutingResultResponse();
        ScoutingResult scoutingResult = scoutReport.getInfoReceiveModel();
        if(Objects.isNull(scoutingResult)) {
            return scoutReportResponseDetail.setScoutingResult(scoutingResultResponse);
        }
        // map userProfile
        scoutingResultResponse.setUsername(scoutingResult.getUsername())
                              .setAvatarUrl(Objects.nonNull(scoutingResult.getAvatarUrl()) ? assetService.getUrl(scoutingResult.getAvatarUrl()) : null)
                              .setLocation(new Location().setX(scoutingResult.getX()).setY(scoutingResult.getY()));

        // MissionResult : SUCCESS,FAIL,BETRAYED,COUNTER_SCOUT, NOT_FOUND_ENEMY_BASE;
        if (scoutReport.getResult().equals(MissionResult.FAIL)
            || scoutReport.getResult().equals(MissionResult.NOT_FOUND_ENEMY_BASE)) {
            scoutingResultResponse.setSoliderDie(scoutingResult.getSoliderDie());

        } else if (scoutReport.getResult().equals(MissionResult.BETRAYED)) {
            scoutingResultResponse.setSoliderDie(scoutingResult.getSoliderDie());

            switch (scoutReport.getMissionType()) {
                case CONNECTION_STATUS:
                    scoutingResultResponse.setInOnline(scoutingResult.getInOnline())
                                          .setLastActiveFrom(scoutingResult.getLastActiveFrom());
                    break;
                case ASSETS:
                    scoutingResultResponse.setGold(scoutingResult.getGold())
                                          .setWood(scoutingResult.getWood())
                                          .setStone(scoutingResult.getStone())
                                          .setQueens(getListQueenConfigResponse(scoutingResult.getQueens()))
                                          .setRelics(getListRelicConfigResponse(scoutingResult.getRelics()));
                    break;
                case MILITARY:
                    scoutingResultResponse.setEscortShips(updateThumbnailForShipScoutingResult(scoutingResult.getEscortShips()));
                    scoutingResultResponse.setMotherShips(updateThumbnailForShipScoutingResult(scoutingResult.getMotherShips()));
                    break;
                default:

            }
        } else { // SUCCESS
            switch (scoutReport.getMissionType()) {
                case CONNECTION_STATUS:
                    scoutingResultResponse.setInOnline(scoutingResult.getInOnline())
                                          .setLastActiveFrom(scoutingResult.getLastActiveFrom());
                    break;
                case ASSETS:
                    scoutingResultResponse.setGold(scoutingResult.getGold())
                                          .setWood(scoutingResult.getWood())
                                          .setStone(scoutingResult.getStone())
                                          .setQueens(getListQueenConfigResponse(scoutingResult.getQueens()))
                                          .setRelics(getListRelicConfigResponse(scoutingResult.getRelics()));
                    break;
                case MILITARY:
                    scoutingResultResponse.setEscortShips(updateThumbnailForShipScoutingResult(scoutingResult.getEscortShips()));
                    scoutingResultResponse.setMotherShips(updateThumbnailForShipScoutingResult(scoutingResult.getMotherShips()));
                    break;
                default:

            }
        }
        return scoutReportResponseDetail.setScoutingResult(scoutingResultResponse);
    }

    public List<QueenConfigResponse> getListQueenConfigResponse(List<Long> ids) {
        List<QueenConfig> queenConfigList = queenConfigDataSource.getAll();
        Map<Long, QueenConfig> map = new HashMap<Long, QueenConfig>();
        for (QueenConfig queenConfig : queenConfigList) {
            map.put(queenConfig.getId(), queenConfig);
        }
        List<QueenConfigResponse> responseList = new ArrayList<>();
        QueenConfig queenConfig;
        for (Long id : ids) {

            queenConfig = map.get(id);
            if (queenConfig != null) {
                QueenConfigResponse queenConfigResponse = new QueenConfigResponse();
                queenConfigResponse.setId(queenConfig.getId())
                                   .setMp(queenConfig.getMp())
                                   .setThumbnail(assetService.getUrl(queenConfig.getThumbnail()))
                                   .setName(queenConfig.getName());
                responseList.add(queenConfigResponse);
            }

        }
        return responseList;
    }

    public List<RelicConfigResponse> getListRelicConfigResponse(List<Long> ids) {
        List<RelicConfig> relicConfigs = relicConfigDataSource.getAll();
        Map<Long, RelicConfig> map = new HashMap<Long, RelicConfig>();
        for (RelicConfig relicConfig : relicConfigs) {
            map.put(relicConfig.getId(), relicConfig);
        }
        List<RelicConfigResponse> responseList = new ArrayList<>();
        RelicConfig relicConfig;
        for (Long id : ids) {
            relicConfig = map.get(id);
            if (relicConfig != null) {
                RelicConfigResponse relicConfigResponse = new RelicConfigResponse();
                relicConfigResponse.setId(relicConfig.getId())
                                   .setName(relicConfig.getName())
                                   .setMp(relicConfig.getRelicMpConfig().getMp())
                                   .setThumbnail(assetService.getUrl(relicConfig.getThumbnail()))
                                   .setLevel(relicConfig.getRelicMpConfig().getLevel());
                responseList.add(relicConfigResponse);
            }

        }
        return responseList;
    }

    public <T extends ShipScoutingResult> List<T> updateThumbnailForShipScoutingResult(List<T> list) {
        return list.stream().map(is -> {
            if (Objects.nonNull(is.getThumbnail())) {
                is.setThumbnail(assetService.getUrl(is.getThumbnail()));
            }
            return is;
        }).collect(Collectors.toList());
    }

}

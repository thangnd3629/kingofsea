package com.supergroup.kos.api.seamap;

import java.util.Objects;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.supergroup.asset.service.AssetService;
import com.supergroup.auth.domain.model.User;
import com.supergroup.auth.domain.model.UserProfile;
import com.supergroup.auth.domain.service.UserProfileService;
import com.supergroup.kos.building.domain.command.KosProfileCommand;
import com.supergroup.kos.building.domain.command.UserCommand;
import com.supergroup.kos.building.domain.constant.seamap.UserBaseStatus;
import com.supergroup.kos.building.domain.model.profile.KosProfile;
import com.supergroup.kos.building.domain.model.seamap.KosAlliance;
import com.supergroup.kos.building.domain.model.seamap.KosWarInfo;
import com.supergroup.kos.building.domain.model.seamap.SeaElement;
import com.supergroup.kos.building.domain.model.seamap.UserBase;
import com.supergroup.kos.building.domain.service.building.CastleBuildingService;
import com.supergroup.kos.building.domain.service.profile.KosProfileService;
import com.supergroup.kos.building.domain.service.seamap.AllianceService;
import com.supergroup.kos.building.domain.service.seamap.KosWarInfoService;
import com.supergroup.kos.building.domain.service.seamap.SeaElementService;
import com.supergroup.kos.building.domain.service.seamap.UserBaseService;
import com.supergroup.kos.building.domain.service.seamap.activity.SeaActivityService;
import com.supergroup.kos.dto.battle.InvaderResponse;
import com.supergroup.kos.dto.profile.UserInfoResponse;
import com.supergroup.kos.dto.seamap.alliance.AllianceInfoResponse;
import com.supergroup.kos.dto.seamap.elements.ElementResponse;
import com.supergroup.kos.dto.seamap.userbase.UserBaseInfoResponse;
import com.supergroup.kos.mapper.battle.InvaderMapper;
import com.supergroup.kos.util.AuthUtil;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/seamap/userbase")
@RequiredArgsConstructor
public class UserBaseRestController {
    private final AllianceService       allianceService;
    private final UserProfileService    userProfileService;
    private final KosProfileService     kosProfileService;
    private final CastleBuildingService castleBuildingService;
    private final UserBaseService       userBaseService;
    private final KosWarInfoService     kosWarInfoService;
    private final AssetService          assetService;
    private final SeaActivityService    seaActivityService;
    private final SeaElementService     seaElementService;
    private final InvaderMapper         invaderMapper;

    @GetMapping("")
    public ResponseEntity<UserBaseInfoResponse> getUserBaseInfo(@RequestParam(name = "kosProfileId", required = false) Long kosProfileId) {
        KosProfile kosProfile;
        if (Objects.nonNull(kosProfileId)) {
            kosProfile = kosProfileService.getKosProfileById(kosProfileId);
        } else {
            kosProfile = kosProfileService.getKosProfile(new UserCommand().setUserId(AuthUtil.getUserId()));
        }
        UserBaseInfoResponse result = new UserBaseInfoResponse();
        UserBase userBase = userBaseService.getByKosProfileId(kosProfile.getId());
        User user = kosProfile.getUser();
        UserProfile userProfile = userProfileService.findByUserId(user.getId());
        KosAlliance alliance = allianceService.getUserAlliance(kosProfile.getId());
        Double power = kosWarInfoService.getTotalPower(kosProfile.getId());
        KosWarInfo kosWarInfo = kosWarInfoService.getByKosProfileId(kosProfile.getId());
        UserInfoResponse userInfoResponse = new UserInfoResponse();
        userInfoResponse.setId(user.getId())
                        .setKosProfileId(kosProfile.getId())
                        .setEmail(user.getEmail())
                        .setUsername(userProfile.getUsername())
                        .setAvatarUrl(assetService.getUrl(userProfile.getAvatarUrl()));
        AllianceInfoResponse allianceInfo = new AllianceInfoResponse();
        if (Objects.nonNull(alliance)) {
            allianceInfo.setDescription(alliance.getDescription()).setId(alliance.getId());
        }
        SeaElement seaElement = userBaseService.getByKosProfileId(kosProfile.getId());
        result.setUserInfo(userInfoResponse)
              .setName(userBase.getIslandName())
              .setPower(power)
              .setLose(kosWarInfo.getLose())
              .setWin(kosWarInfo.getWin())
              .setLevel(castleBuildingService.getCastleBuilding(new KosProfileCommand().setKosProfileId(kosProfile.getId())).getLevel())
              .setBossKilled(kosWarInfo.getBossKilled())
              .setWarshipDestroyed(kosWarInfo.getWarshipsDestroyed())
              .setWarshipLost(kosWarInfo.getWarshipsLost())
              .setAlliance(allianceInfo)
              .setElement(
                      new ElementResponse()
                              .setId(seaElement.getId())
                              .setElementId(userBase.getSeaElementConfig().getId())
                              .setX(userBase.getCoordinates().getX())
                              .setY(userBase.getCoordinates().getY())
                         );
        // check occupy status
        if (seaElement.isOccupied()) {
            result.getElement().setStatus(UserBaseStatus.OCCUPIED.name());
        } else {
            result.getElement().setStatus(UserBaseStatus.NORMAL.name());
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/invader")
    public ResponseEntity<InvaderResponse> invaderInfo() {
        var kosProfile = kosProfileService.getKosProfile(new UserCommand().setUserId(AuthUtil.getUserId()));
        var userBase = userBaseService.getByKosProfileId(kosProfile.getId());
        var invader = seaElementService.invader(userBase.getId());
        invader.setActivitiesOnOccupiedBase(
                seaActivityService.findByElementIdAndKosProfileId(kosProfile.getBase().getId(), invader.getKosProfileInvader().getId()));
        return ResponseEntity.ok(invaderMapper.map(invader));
    }

}
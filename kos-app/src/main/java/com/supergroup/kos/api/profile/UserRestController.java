package com.supergroup.kos.api.profile;

import java.util.Objects;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.supergroup.asset.service.AssetService;
import com.supergroup.auth.domain.command.UpdateUserProfileCommand;
import com.supergroup.auth.domain.service.UserProfileService;
import com.supergroup.auth.domain.service.UserService;
import com.supergroup.kos.building.domain.command.SaveOrUpdateElementCommand;
import com.supergroup.kos.building.domain.command.UserCommand;
import com.supergroup.kos.building.domain.model.scout.Location;
import com.supergroup.kos.building.domain.model.seamap.UserBase;
import com.supergroup.kos.building.domain.service.profile.KosProfileService;
import com.supergroup.kos.building.domain.service.seamap.MapService;
import com.supergroup.kos.building.domain.service.seamap.UserBaseService;
import com.supergroup.kos.config.AccessSession;
import com.supergroup.kos.dto.auth.UpdateUserInfoRequest;
import com.supergroup.kos.dto.profile.UserInfoResponse;
import com.supergroup.kos.util.AuthUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/v1/user")
@RequiredArgsConstructor
@Slf4j
public class UserRestController {
    private final UserService        userService;
    private final UserBaseService    userBaseService;
    private final UserProfileService userProfileService;
    private final AssetService       assetService;
    private final KosProfileService  kosProfileService;
    private final MapService         mapService;

    @GetMapping("/info")
    public ResponseEntity<UserInfoResponse> getInfo() {
        var user = userService.findUserByEmail(AuthUtil.getCurrentUserDetails().getUsername());
        var kosProfile = kosProfileService.getKosProfile(new UserCommand().setUserId(user.getId()));
        var userProfile = userProfileService.findByUserId(user.getId());
        UserInfoResponse userInfoResponse = new UserInfoResponse();
        userInfoResponse.setId(user.getId())
                        .setKosProfileId(kosProfile.getId())
                        .setEmail(user.getEmail())
                        .setUsername(userProfile.getUsername())
                        .setAvatarUrl(assetService.getUrl(userProfile.getAvatarUrl()));
        UserBase userBase = userBaseService.getByKosProfileId(kosProfile.getId());
        userInfoResponse.setLocation(new Location().setX(userBase.getCoordinates().getX())
                                                   .setY(userBase.getCoordinates().getY()));
        return ResponseEntity.ok(userInfoResponse);
    }

    @PutMapping("/info")
    public ResponseEntity<?> updateUserInfo(@RequestBody UpdateUserInfoRequest request) {
        var userId = ((AccessSession) AuthUtil.getCurrentUserDetails()).getUserId();
        var userProfile = userProfileService.findByUserId(userId);
        userProfileService.updateUserProfile(new UpdateUserProfileCommand().setUserProfile(userProfile)
                                                                           .setUsername(request.getUsername())
                                                                           .setAvatarId(request.getAvatarId()));
        // rename user base's name after change username
        if (Objects.nonNull(request.getUsername()) && !request.getUsername().isEmpty()) {
            var kosProfile = kosProfileService.getKosProfile(new UserCommand().setUserId(userId));
            kosProfile.getBase().setIslandName(userProfile.getUsername());
            mapService.saveOrUpdateElement(new SaveOrUpdateElementCommand(kosProfile.getBase()));
        }
        return ResponseEntity.noContent().build();
    }

}

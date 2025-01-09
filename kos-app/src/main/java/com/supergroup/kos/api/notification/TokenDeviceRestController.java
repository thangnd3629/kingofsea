package com.supergroup.kos.api.notification;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.supergroup.auth.domain.service.LoginSessionService;
import com.supergroup.core.constant.ErrorCode;
import com.supergroup.core.exception.KOSException;
import com.supergroup.kos.config.AccessSession;
import com.supergroup.kos.dto.notification.TokenDeviceRequest;
import com.supergroup.kos.notification.domain.command.RegisterNotificationCommand;
import com.supergroup.kos.notification.domain.service.UserNotifyRegistrationService;
import com.supergroup.kos.util.AuthUtil;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/user/token-device")
@RequiredArgsConstructor
public class TokenDeviceRestController {
    private final UserNotifyRegistrationService userNotifyRegistrationService;
    private final LoginSessionService           loginSessionService;

    @PostMapping("")
    public ResponseEntity<?> register(@Valid @RequestBody TokenDeviceRequest token) {
        var accessSession = (AccessSession) AuthUtil.getCurrentUserDetails();
        var loginSession = loginSessionService.findById(accessSession.getLoginSessionId())
                                              .orElseThrow(() -> KOSException.of(ErrorCode.USER_IS_LOGGED_OUT));
        userNotifyRegistrationService.registerNotification(new RegisterNotificationCommand(token.getTokenDevice(), loginSession));
        return ResponseEntity.ok().build();
    }

}

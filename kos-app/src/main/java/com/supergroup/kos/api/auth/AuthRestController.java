package com.supergroup.kos.api.auth;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.supergroup.auth.domain.command.RefreshAccessTokenCommand;
import com.supergroup.auth.domain.command.ResetPasswordCommand;
import com.supergroup.auth.domain.command.VerifyChangeEmailCommand;
import com.supergroup.auth.domain.command.VerifyRegistrationCommand;
import com.supergroup.auth.domain.service.AuthService;
import com.supergroup.auth.domain.service.UserService;
import com.supergroup.core.constant.ErrorCode;
import com.supergroup.core.exception.KOSException;
import com.supergroup.kos.building.domain.async.AuthenticationAsyncTask;
import com.supergroup.kos.building.domain.service.battle.BattlePvPService;
import com.supergroup.kos.config.AccessSession;
import com.supergroup.kos.constant.HeaderKey;
import com.supergroup.kos.dto.auth.ChangeEmailRequest;
import com.supergroup.kos.dto.auth.ChangeEmailResponse;
import com.supergroup.kos.dto.auth.ChangePasswordRequest;
import com.supergroup.kos.dto.auth.ForgotPasswordRequest;
import com.supergroup.kos.dto.auth.ForgotPasswordResponse;
import com.supergroup.kos.dto.auth.LoginByUsernameAndPasswordRequest;
import com.supergroup.kos.dto.auth.LoginResponse;
import com.supergroup.kos.dto.auth.RefreshTokenResponse;
import com.supergroup.kos.dto.auth.RegisterRequest;
import com.supergroup.kos.dto.auth.RegisterResponse;
import com.supergroup.kos.dto.auth.ResetPasswordRequest;
import com.supergroup.kos.dto.auth.VerifyChangeEmailRequest;
import com.supergroup.kos.dto.auth.VerifyRequest;
import com.supergroup.kos.mapper.RequestMapper;
import com.supergroup.kos.util.AuthUtil;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthRestController {
    private final RequestMapper           requestMapper;
    private final AuthService             authService;
    private final UserService             userService;
    private final AuthenticationAsyncTask authenticationAsyncTask;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        var registerCommand = requestMapper.toCommand(request);
        var verifySession = authService.register(registerCommand);
        return ResponseEntity.ok(new RegisterResponse(verifySession.getToken()));
    }

    @PostMapping("/register/verify")
    public ResponseEntity<LoginResponse> verifyRegistration(
            @Valid @NotEmpty @NotBlank @RequestHeader(name = HeaderKey.VERIFY_TOKEN) String verifyToken,
            @Valid @RequestBody VerifyRequest verifyRequest) {
        var loginSession = authService.verifyRegistration(new VerifyRegistrationCommand(verifyToken, verifyRequest.getOtp()));
        return ResponseEntity.ok(new LoginResponse(loginSession.getAccessToken(), loginSession.getRefreshToken()));
    }

    private final BattlePvPService battlePvPService;

    @PostMapping("login")
    public ResponseEntity<LoginResponse> loginByUsernameAndPassword(@RequestBody @Valid LoginByUsernameAndPasswordRequest request) {
        var loginSession = authService.loginWithUsernameAndPassword(requestMapper.toCommand(request), userId -> {
            authenticationAsyncTask.sendOtherLocationLoginNotification(userId);
            return null;
        });
        return ResponseEntity.ok(new LoginResponse(loginSession.getAccessToken(), loginSession.getRefreshToken()));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        var accessSession = (AccessSession) AuthUtil.getCurrentUserDetails();
        authService.logout(accessSession.getLoginSessionId());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request) {
        var user = userService.findByEmail(AuthUtil.getCurrentUserDetails().getUsername())
                              .orElseThrow(() -> KOSException.of(ErrorCode.USER_NOT_FOUND));
        authService.changePassword(requestMapper.toCommand(request, user));
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/password/forgot")
    public ResponseEntity<ForgotPasswordResponse> forgotPasswordByEmail(@RequestBody @Valid ForgotPasswordRequest request) {
        var session = authService.forgotPasswordByEmail(requestMapper.toCommand(request));
        return ResponseEntity.ok(new ForgotPasswordResponse(session.getToken()));
    }

    @PutMapping("/password/reset")
    public ResponseEntity<?> verifyAndResetPassword(@Valid @RequestBody ResetPasswordRequest request,
                                                    @Valid @NotEmpty @NotBlank @RequestHeader(HeaderKey.VERIFY_TOKEN) String verifyToken) {
        var resetPasswordCommand = new ResetPasswordCommand(verifyToken, request.getNewPassword(), request.getOtp());
        authService.verifyAndResetPassword(resetPasswordCommand);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestHeader(HeaderKey.REFRESH_TOKEN) String refreshToken) {
        var loginSession = authService.refreshAccessToken(new RefreshAccessTokenCommand(refreshToken));
        return ResponseEntity.ok(new RefreshTokenResponse(
                loginSession.getAccessToken(),
                loginSession.getRefreshToken()
        ));
    }

    @PutMapping("/email")
    public ResponseEntity<ChangeEmailResponse> changeEmail(@Valid @RequestBody ChangeEmailRequest request) {
        var user = userService.findById(((AccessSession) AuthUtil.getCurrentUserDetails()).getUserId())
                              .orElseThrow(() -> KOSException.of(ErrorCode.USER_NOT_FOUND));
        var session = authService.changeEmail(requestMapper.toCommand(request, user));
        return ResponseEntity.ok(new ChangeEmailResponse(session.getToken()));
    }

    @PutMapping("/email/verify")
    public ResponseEntity<?> verifyChangeEmail(@Valid @NotEmpty @NotBlank @RequestHeader(name = HeaderKey.VERIFY_TOKEN) String verifyToken,
                                               @Valid @RequestBody VerifyChangeEmailRequest request) {
        authService.verifyChangeEmail(new VerifyChangeEmailCommand(verifyToken, request.getOtp()));
        return ResponseEntity.noContent().build();
    }
}

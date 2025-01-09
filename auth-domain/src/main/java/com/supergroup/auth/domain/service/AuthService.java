package com.supergroup.auth.domain.service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.supergroup.auth.domain.command.ChangeEmailCommand;
import com.supergroup.auth.domain.command.ChangePasswordCommand;
import com.supergroup.auth.domain.command.ForgotPasswordCommand;
import com.supergroup.auth.domain.command.LoginWithUsernameAndPasswordCommand;
import com.supergroup.auth.domain.command.RefreshAccessTokenCommand;
import com.supergroup.auth.domain.command.RegisterCommand;
import com.supergroup.auth.domain.command.ResetPasswordCommand;
import com.supergroup.auth.domain.command.VerifyChangeEmailCommand;
import com.supergroup.auth.domain.command.VerifyRegistrationCommand;
import com.supergroup.auth.domain.constant.UserTag;
import com.supergroup.auth.domain.constant.VerifyReason;
import com.supergroup.auth.domain.model.LoginSession;
import com.supergroup.auth.domain.model.Registration;
import com.supergroup.auth.domain.model.User;
import com.supergroup.auth.domain.model.UserProfile;
import com.supergroup.auth.domain.model.VerifySession;
import com.supergroup.auth.domain.provider.token.JwtTokenProvider;
import com.supergroup.auth.domain.repository.persistence.DefaultAvatarRepository;
import com.supergroup.auth.domain.repository.persistence.RegistrationRepository;
import com.supergroup.auth.domain.repository.persistence.UserCacheRepository;
import com.supergroup.auth.domain.repository.persistence.UserProfileRepository;
import com.supergroup.auth.domain.util.EmailNormalizer;
import com.supergroup.auth.domain.util.OTPUtils;
import com.supergroup.auth.domain.util.PasswordUtil;
import com.supergroup.core.constant.ErrorCode;
import com.supergroup.core.exception.KOSException;

import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserProfileRepository   userProfileRepository;
    private final LoginSessionService     loginSessionService;
    private final UserService             userService;
    private final VerifySessionService    verifySessionService;
    private final RegistrationRepository  registrationRepository;
    private final SendEmailAsync          sendEmailAsync;
    private final JwtTokenProvider        jwtTokenProvider;
    private final UserCacheRepository     userCacheRepository;
    private final DefaultAvatarRepository defaultAvatarRepository;
    private final Environment             environment;

    @Value("${verify-session.register.attempts}")
    private Long MAX_ATTEMPTS;
    @Value("${login-session.timeout}")
    private Long EXPIRY_LOGIN_SESSION;
    @Value("${login.attempts}")
    private Long LOGIN_FAIL_ATTEMPTS;
    @Value("${login.lock-time-duration}")
    private Long LOCK_TIME;

    /**
     * This method will register a registration, the registration will be active and become an Account when
     * the number phone is verified
     */
    @Transactional
    public VerifySession register(RegisterCommand command) {
        // check if email existed, throw exception
        if (userService.existsByOriginEmail(EmailNormalizer.normalize(command.getEmail()))) {
            throw KOSException.of(ErrorCode.EMAIL_IS_EXISTED);
        }

        // check exist registration, if it is not valid, delete it
        var existRegistration = registrationRepository.findRegistrationByEmail(command.getEmail());
        existRegistration.ifPresent(registration -> {
            verifySessionService.findByAccountIdAndReason(registration.getAccountId(), VerifyReason.REGISTER).ifPresent(verifySession -> {
                if (verifySessionService.isExpiredSession(verifySession)) {
                    // delete if it is expired
                    registrationRepository.delete(registration);
                    verifySessionService.deactivate(verifySession);
                } else {
                    throw KOSException.of(ErrorCode.USER_IS_VERIFYING);
                }
            });
        });

        // if valid, create session registration
        Registration registration = createRegistration(command.getEmail(), command.getPassword(), command.getUsername());
        // create verify session
        VerifySession verifySession = verifySessionService.createVerifySession(registration, VerifyReason.REGISTER, OTPUtils.code(), null);
        sendEmailAsync.sendRegisterVerifyOTP(registration, verifySession.getOtp());
        return verifySession;
    }

    /**
     * This method will check user's otp, if it is valid, registration will be become to account
     */
    @Transactional(dontRollbackOn = KOSException.class)
    public LoginSession verifyRegistration(VerifyRegistrationCommand command) throws KOSException {

        var verifyTokenClaim = jwtTokenProvider.getVerifyTokenClaim(command.getToken());
        var verifySession = verifySessionService.findByIdAndReason(verifyTokenClaim.getVerifyId(), VerifyReason.REGISTER).orElseThrow(
                () -> KOSException.of(ErrorCode.VERIFY_SESSION_INVALID));
        var registration = registrationRepository.findById(verifyTokenClaim.getAccountId()).orElseThrow(
                () -> KOSException.of(ErrorCode.REGISTRATION_SESSION_NOT_FOUND));
        // check attempts, if it is not valid, delete registration
        if (verifySession.getAttempts() >= MAX_ATTEMPTS) {
            throw KOSException.of(ErrorCode.VERIFY_ATTEMPT_EXCEEDED);
        }

        if (verifySession.getExpirationDate().isBefore(LocalDateTime.now())) {
            verifySessionService.deactivate(verifySession);
            registrationRepository.delete(registration);
            throw KOSException.of(ErrorCode.VERIFY_SESSION_EXPIRED);
        }

        // check otp if wrong, increase attempt
        if (!verifySession.getOtp().equals(command.getOtp())) {
            verifySession.setAttempts(verifySession.getAttempts() + 1);
            verifySessionService.save(verifySession);
            throw KOSException.of(ErrorCode.OTP_IS_INCORRECT);
        }
        // This case otp is right and attempts is valid
        // create new user
        var user = userService.create(registration, UserTag.REAL);
        // creat new profile
        var defaultAvatar = defaultAvatarRepository.findById(1L).orElseThrow(() -> KOSException.of(ErrorCode.SERVER_ERROR));
        var userProfile = new UserProfile().setAvatarUrl(defaultAvatar.getAssetId()).setUsername(registration.getUsername()).setUser(user);
        userProfileRepository.save(userProfile);
        registrationRepository.delete(registration);
        return loginSessionService.create(user, EXPIRY_LOGIN_SESSION, TimeUnit.MINUTES);
    }

    /**
     * Login with username and password
     */
    @Transactional(dontRollbackOn = { KOSException.class })
    public LoginSession loginWithUsernameAndPassword(LoginWithUsernameAndPasswordCommand command, Function<Long, Void> onAuthenticatedOtherDevice) {
        User existUser = userService.findByOriginEmail(EmailNormalizer.normalize(command.getEmail())).orElseThrow(
                () -> KOSException.of(ErrorCode.USER_NOT_FOUND));

        if (!existUser.isEnabled()) {
            throw KOSException.of(ErrorCode.USER_WAS_DELETED_OR_BANNED);
        }

        if (Objects.nonNull(existUser.getLockTime()) && existUser.getLockTime().isAfter(LocalDateTime.now())) {
            throw KOSException.of(ErrorCode.TOO_MANY_WRONG_LOGIN);
        } else if (existUser.getIsLocked() && existUser.getLoginFailAttempts() >= 5) {
            userService.unlock(existUser);
            existUser = userService.save(existUser).orElseThrow();
        }

        var passwordUtil = new PasswordUtil();
        if (passwordUtil.check(command.getRawPassword(), existUser.getPassword())) {
            // when login success, unlock user who is locked when try login
            userService.unlock(existUser);
            userService.save(existUser);
            // if prod env or staging env, delete all login session before create new one
            // ONE LOGIN SESSION AT TIME
            if (Arrays.stream(environment.getActiveProfiles()).allMatch(s -> s.equals("prod") || s.equals("staging"))) {
                // delete all before login session
                loginSessionService.deactivateAllLoginSession(existUser.getId());
            }
            var loginSession = loginSessionService.create(existUser, EXPIRY_LOGIN_SESSION, TimeUnit.MINUTES);
            // if prod env or staging env, send notification for old login session, notify have new login session
            if (Arrays.stream(environment.getActiveProfiles()).allMatch(s -> s.equals("prod") || s.equals("staging"))) {
                // send logout message to other login session
                var existedLoginSession = loginSessionService.findByUserId(existUser.getId())
                                                             .stream()
                                                             .filter((e) -> !e.getId().equals(loginSession.getId()))
                                                             .collect(Collectors.toList())
                                                             .stream().findFirst();
                if (existedLoginSession.isPresent()) {
                    onAuthenticatedOtherDevice.apply(existUser.getId());
                }
            }
            return loginSession;
        } else {
            // when user try login over attempts. Lock it
            // else notify login fail and increase attempt
            if (Objects.nonNull(existUser.getLoginFailAttempts()) && existUser.getLoginFailAttempts() >= LOGIN_FAIL_ATTEMPTS) {
                userService.lock(existUser, LOCK_TIME);
                userService.save(existUser);
                throw KOSException.of(ErrorCode.TOO_MANY_WRONG_LOGIN);
            } else {
                if (Objects.isNull(existUser.getLoginFailAttempts())) {
                    existUser.setLoginFailAttempts(0L);
                }
                existUser.setLoginFailAttempts(existUser.getLoginFailAttempts() + 1);
                userService.save(existUser);
                throw KOSException.of(ErrorCode.PASSWORD_IS_WRONG);
            }
        }
    }

    /**
     * logout by delete login session
     */
    @Transactional
    public void logout(Long loginSessionId) {
        loginSessionService.deactivateSessionById(loginSessionId);
    }

    /**
     * @param accessToken: access token
     */
    public Boolean isLogout(String accessToken) {
        var accessTokenClaim = jwtTokenProvider.getAccessTokenClaim(accessToken);
        return !loginSessionService.isActive(accessTokenClaim.getUuid());
    }

    /**
     * Forgot password
     * Create verify session and send it to email
     */
    @Transactional
    public VerifySession forgotPasswordByEmail(ForgotPasswordCommand command) {
        var user = userService.findByEmail(command.getEmail()).orElseThrow(() -> KOSException.of(ErrorCode.USER_NOT_FOUND));
        var session = verifySessionService.createVerifySession(user, VerifyReason.FORGOT_PASSWORD, OTPUtils.code(), null);
        var username = userProfileRepository.findByUserId(user.getId()).orElseThrow(() -> KOSException.of(ErrorCode.USER_PROFILE_NOT_FOUND))
                                            .getUsername();
        sendEmailAsync.sendForgotPasswordVerifyByEmail(command.getEmail(), username, session.getOtp());
        return session;
    }

    /**
     * Change password
     * Check old password, then encode password and set to user
     */
    @Transactional
    public void changePassword(ChangePasswordCommand command) {
        var passwordUtil = new PasswordUtil();
        if (passwordUtil.check(command.getOldPassword(), command.getUser().getPassword())) {
            command.getUser().setPassword(passwordUtil.encode(command.getNewPassword()));
            userService.save(command.getUser());
        } else {
            throw KOSException.of(ErrorCode.PASSWORD_IS_WRONG);
        }
    }

    /**
     * Change email
     * check password, send verify session to new email
     */
    @Transactional
    public VerifySession changeEmail(ChangeEmailCommand command) {
        // check email is existed?
        if (userService.checkExistEmail(command.getNewEmail())) {
            throw KOSException.of(ErrorCode.EMAIL_IS_EXISTED);
        }
        var session = verifySessionService.createVerifySession(command.getUser(), VerifyReason.CHANGE_EMAIL, OTPUtils.code(), command.getNewEmail());
        var username = userProfileRepository.findByUserId(command.getUser().getId()).orElseThrow(
                () -> KOSException.of(ErrorCode.USER_PROFILE_NOT_FOUND)).getUsername();
        sendEmailAsync.sendChangeEmailVerifyByEmail(command.getNewEmail(), username, session.getOtp());
        return session;
    }

    /**
     * Verify session change email
     * and set new email
     */
    @Transactional(dontRollbackOn = { KOSException.class })
    public void verifyChangeEmail(VerifyChangeEmailCommand command) {
        var claims = jwtTokenProvider.getBodyToken(command.getVerifyToken());
        var user = userService.findById(claims.get(JwtTokenProvider.ACCOUNT_ID, Long.class)).orElseThrow(
                () -> KOSException.of(ErrorCode.USER_NOT_FOUND));
        var verifySession = verifySessionService.findById(claims.get(JwtTokenProvider.VERIFY_ID, Long.class)).orElseThrow(
                () -> KOSException.of(ErrorCode.VERIFY_SESSION_INVALID));
        if (verifySessionService.isExpiredSession(verifySession)) {
            throw KOSException.of(ErrorCode.VERIFY_SESSION_EXPIRED);
        }

        if (Objects.nonNull(verifySession.getAttempts()) && verifySession.getAttempts() > 3) {
            verifySessionService.delete(verifySession);
            throw KOSException.of(ErrorCode.VERIFY_SESSION_INVALID);
        } else {
            if (!verifySession.getOtp().equals(command.getOtp())) {
                if (Objects.nonNull(verifySession.getAttempts())) {
                    verifySession.setAttempts(0L);
                }
                verifySession.setAttempts(verifySession.getAttempts() + 1);
                throw KOSException.of(ErrorCode.OTP_IS_INCORRECT);
            }
            var email = claims.get(JwtTokenProvider.NEW_EMAIL, String.class);
            user.setEmail(email);
            user.setOriginEmail(EmailNormalizer.normalize(EmailNormalizer.normalize(email)));
            userService.save(user);
            userCacheRepository.refreshUser(user.getId());
        }
    }

    /**
     * Verify reset password session and reset password
     */
    @Transactional(dontRollbackOn = KOSException.class)
    public void verifyAndResetPassword(ResetPasswordCommand command) {
        var verifyTokenClaim = jwtTokenProvider.getVerifyTokenClaim(command.getVerifyToken());
        var user = userService.findById(verifyTokenClaim.getAccountId()).orElseThrow(() -> KOSException.of(ErrorCode.USER_NOT_FOUND));
        var session = verifySessionService.getById(verifyTokenClaim.getVerifyId());
        if (!command.getOtp().equals(session.getOtp())) {
            throw KOSException.of(ErrorCode.OTP_IS_INCORRECT);
        }
        if (verifySessionService.isExpiredSession(session)) {
            verifySessionService.deactivate(session);
            throw KOSException.of(ErrorCode.VERIFY_SESSION_EXPIRED);
        }
        userService.updatePassword(user, command.getNewPassword());
        verifySessionService.deactivate(session);
        forceLogout(session.getAccountId());
    }

    /**
     * Generate new access token base on refresh token
     */
    @Transactional
    public LoginSession refreshAccessToken(RefreshAccessTokenCommand command) {
        try {
            var refreshTokenClaim = jwtTokenProvider.getRefreshTokenClaim(command.getRefreshToken());
            var loginSession = loginSessionService.findById(refreshTokenClaim.getSessionId()).orElseThrow(
                    () -> KOSException.of(ErrorCode.USER_IS_LOGGED_OUT));
            loginSessionService.refresh(loginSession);
            return loginSession;
        } catch (ExpiredJwtException ex) {
            throw KOSException.of(ErrorCode.TOKEN_EXPIRED);
        }
    }

    private Registration createRegistration(String email, String password, String username) {
        var passwordUtil = new PasswordUtil();
        return registrationRepository.save(new Registration().setUsername(username).setOriginEmail(EmailNormalizer.normalize(email)).setEmail(email)
                                                             .setPassword(passwordUtil.encode(password)));

    }

    private void forceLogout(Long userId) {
        loginSessionService.deactivateAllLoginSession(userId);
        userCacheRepository.refreshUser(userId);
    }

}

package com.supergroup.auth.domain.service;

import java.time.LocalDateTime;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.supergroup.auth.domain.constant.UserTag;
import com.supergroup.auth.domain.model.Registration;
import com.supergroup.auth.domain.model.User;
import com.supergroup.auth.domain.repository.persistence.UserRepository;
import com.supergroup.auth.domain.util.EmailNormalizer;
import com.supergroup.auth.domain.util.PasswordUtil;
import com.supergroup.auth.domain.validator.InputFieldValidator;
import com.supergroup.core.constant.ErrorCode;
import com.supergroup.core.exception.KOSException;

import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements UserDetailsService {
    @Delegate
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email).orElseThrow(() -> KOSException.of(ErrorCode.USER_NOT_FOUND));
    }

    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> KOSException.of(ErrorCode.USER_NOT_FOUND));
    }

    public Optional<User> save(User user) {
        return Optional.of(userRepository.save(user));
    }

    /**
     * Create new user from registration
     */
    @Transactional
    public User create(Registration registration, UserTag userTag) {
        User user = new User().setPassword(registration.getPassword())
                              .setEmail(registration.getEmail())
                              .setOriginEmail(EmailNormalizer.normalize(registration.getEmail()))
                              .setTag(userTag);
        return userRepository.save(user);
    }

    @Transactional
    public void updatePassword(User user, String newPassword) {
        var passwordUtil = new PasswordUtil();
        user.setPassword(passwordUtil.encode(newPassword));
        save(user);
    }

    /**
     * lock user
     *
     * @param user: user you want to lock
     * @param timeDuration: lock time duration (min)
     * */
    public void lock(User user, Long timeDuration) {
        user.setIsLocked(true);
        user.setLockTime(LocalDateTime.now().plusMinutes(timeDuration));
    }

    /**
     * unlock user
     *
     * @param user: user you want to lock
     */
    public void unlock(User user) {
        user.setIsLocked(false);
        user.setLoginFailAttempts(0L);
        user.setLockTime(null);
    }

    public void validUpdateEmailRequest(String currentEmail, String newEmail) {

        var inputFieldValidator = new InputFieldValidator();

        if (!inputFieldValidator.isValidEmail(newEmail)) {
            throw KOSException.of(ErrorCode.UPDATE_USER_INFO_FAILED);
        }

        if (currentEmail.equals(newEmail)) {
            throw KOSException.of(ErrorCode.NEW_EMAIL_HAS_NO_CHANGE);
        }
        if (checkExistEmail(newEmail)) {
            throw KOSException.of(ErrorCode.EMAIL_IS_EXISTED);
        }
    }

    public void validUpdatePasswordRequest(String userPassword, String currentPassword, String newPassword) {

        var passwordUtil = new PasswordUtil();
        var inputFieldValidator = new InputFieldValidator();

        if (!inputFieldValidator.isValidPassword(newPassword) || !inputFieldValidator.isValidPassword(currentPassword)) {
            throw KOSException.of(ErrorCode.UPDATE_USER_INFO_FAILED);
        }
        if (currentPassword.equals(newPassword)) {
            throw KOSException.of(ErrorCode.NEW_PASSWORD_HAS_NO_CHANGE);
        }
        if (!passwordUtil.check(currentPassword, userPassword)) {
            throw KOSException.of(ErrorCode.CURRENT_PASSWORD_IS_WRONG);
        }
    }

    public void validConfirmPasswordRequest(String userPassword, String currentPassword) {

        var passwordUtil = new PasswordUtil();
        var inputFieldValidator = new InputFieldValidator();

        if (!inputFieldValidator.isValidPassword(currentPassword)) {
            throw KOSException.of(ErrorCode.UPDATE_USER_INFO_FAILED);
        }
        if (!passwordUtil.check(currentPassword, userPassword)) {
            throw KOSException.of(ErrorCode.CURRENT_PASSWORD_IS_WRONG);
        }
    }

    public boolean checkExistEmail(String email) {
        String emailNormalize = EmailNormalizer.normalize(email);
        return existsByOriginEmail(emailNormalize);
    }
}

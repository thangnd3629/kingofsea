package com.supergroup.admin.domain.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.supergroup.admin.domain.command.UpdateStatusCommand;
import com.supergroup.admin.domain.repository.UserRepositoryAdmin;
import com.supergroup.auth.domain.constant.UserStatus;
import com.supergroup.auth.domain.model.User;
import com.supergroup.auth.domain.repository.persistence.UserCacheRepository;
import com.supergroup.core.constant.ErrorCode;
import com.supergroup.core.exception.KOSException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminManageUserService {

    private final UserRepositoryAdmin userRepository;
    private final UserCacheRepository userCacheRepository;

    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> KOSException.of(ErrorCode.USER_NOT_FOUND));
    }

    public User findUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> KOSException.of(ErrorCode.USER_NOT_FOUND));
    }

    public Page<User> getAllUser(String status, Pageable pageable) {
        if (status == null) {
            return userRepository.findAll(pageable);
        }
        return userRepository.findByUserStatus(UserStatus.of(status), pageable);
    }

    public void updateStatusUser(UpdateStatusCommand updateStatusCommand) {
        User user = userRepository.findById(updateStatusCommand.getUserId()).orElseThrow(() -> KOSException.of(ErrorCode.USER_NOT_FOUND));
        user.setUserStatus(updateStatusCommand.getStatus());
        var savedUser = userRepository.save(user);
        userCacheRepository.refreshUser(savedUser.getId());
    }
}

package com.supergroup.auth.domain.service;

import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.supergroup.asset.service.AssetService;
import com.supergroup.auth.domain.command.UpdateUserProfileCommand;
import com.supergroup.auth.domain.model.UserProfile;
import com.supergroup.auth.domain.repository.persistence.DefaultAvatarRepository;
import com.supergroup.auth.domain.repository.persistence.UserProfileRepository;
import com.supergroup.auth.domain.repository.persistence.UserRepository;
import com.supergroup.auth.domain.validator.InputFieldValidator;
import com.supergroup.core.constant.ErrorCode;
import com.supergroup.core.exception.KOSException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserProfileService {
    private final UserProfileRepository   userProfileRepository;
    private final UserRepository          userRepository;
    private final AssetService            assetService;
    private final DefaultAvatarRepository defaultAvatarRepository;

    public UserProfile findByUserId(Long id) {
        return userProfileRepository.findByUserId(id).orElseThrow(() -> KOSException.of(ErrorCode.USER_PROFILE_NOT_FOUND));
    }

    public Optional<UserProfile> save(UserProfile userProfile) {
        return Optional.of(userProfileRepository.save(userProfile));
    }

    @Transactional
    public UserProfile updateUserProfile(UpdateUserProfileCommand command) {
        var inputValidator = new InputFieldValidator();
        if (inputValidator.isValidUsername(command.getUsername())) {
            command.getUserProfile().setUsername(command.getUsername());
        }
        if (Objects.nonNull(command.getAvatarId())) {
            var avatar = defaultAvatarRepository.findById(command.getAvatarId()).orElseThrow(() -> KOSException.of(ErrorCode.BAD_REQUEST_ERROR));
            command.getUserProfile().setAvatarUrl(avatar.getAssetId());
        }
        return userProfileRepository.save(command.getUserProfile());
    }
}

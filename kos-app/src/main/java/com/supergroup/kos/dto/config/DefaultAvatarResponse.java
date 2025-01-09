package com.supergroup.kos.dto.config;

import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class DefaultAvatarResponse {
    private final List<DefaultAvatarItemResponse> listAvatar;
}

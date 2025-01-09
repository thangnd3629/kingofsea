package com.supergroup.kos.dto.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
@Setter
public class DefaultAvatarItemResponse {
    private final Long   id;
    private final String url;
}

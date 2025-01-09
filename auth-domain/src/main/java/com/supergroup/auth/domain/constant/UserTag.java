package com.supergroup.auth.domain.constant;

import java.util.Arrays;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public enum UserTag {
    REAL(0), TEST(1), BOT(2);

    public static UserTag of(Integer tag) {
        return Arrays.stream(UserTag.values()).filter(userStatus -> userStatus.tag.equals(tag))
                     .findFirst().orElseThrow(IllegalArgumentException::new);
    }

    public static Boolean exist(Integer tag) {
        return Arrays.stream(UserTag.values()).anyMatch(userStatus -> userStatus.tag.equals(tag));
    }

    private Integer tag;
}

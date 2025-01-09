package com.supergroup.kos.building.domain.command;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class GetOwnQueensCommand {
    private final Long kosProfileId;
}

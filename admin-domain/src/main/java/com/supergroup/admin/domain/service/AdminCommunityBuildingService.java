package com.supergroup.admin.domain.service;

import java.util.Objects;

import org.springframework.stereotype.Service;

import com.supergroup.admin.domain.command.AdminUpdateCommunityBuildingCommand;
import com.supergroup.core.constant.ErrorCode;
import com.supergroup.core.exception.KOSException;
import com.supergroup.kos.building.domain.repository.persistence.building.CommunityBuildingRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminCommunityBuildingService {

    private final CommunityBuildingRepository communityBuildingRepository;

    public void updateBuilding(AdminUpdateCommunityBuildingCommand command) {
        if(Objects.nonNull(command.getMaxListingRelic())) {
            var building = communityBuildingRepository.findByKosProfileId(command.getKosProfileId())
                                                      .orElseThrow(() -> KOSException.of(ErrorCode.COMMUNITY_BUILDING_IS_NOT_FOUND));
            communityBuildingRepository.save(building.setMaxListingRelic(command.getMaxListingRelic()));
        }
    }
}

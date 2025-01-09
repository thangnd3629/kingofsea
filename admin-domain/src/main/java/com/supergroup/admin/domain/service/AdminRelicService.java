package com.supergroup.admin.domain.service;

import org.springframework.stereotype.Service;

import com.supergroup.admin.domain.command.AdminCreateRelicCommand;
import com.supergroup.core.constant.ErrorCode;
import com.supergroup.core.exception.KOSException;
import com.supergroup.kos.building.domain.command.GetCommunityBuildingInfo;
import com.supergroup.kos.building.domain.model.relic.Relic;
import com.supergroup.kos.building.domain.repository.persistence.relic.RelicConfigRepository;
import com.supergroup.kos.building.domain.repository.persistence.relic.RelicRepository;
import com.supergroup.kos.building.domain.service.building.CommunityBuildingService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminRelicService {
    private final CommunityBuildingService communityBuildingService;
    private final RelicConfigRepository relicConfigRepository;
    private final RelicRepository       relicRepository;

    public Relic createRelic(AdminCreateRelicCommand command) {
        var communityBuilding = communityBuildingService.getBuildingInfo(new GetCommunityBuildingInfo(command.getKosProfileId()));
        var relicConfig = relicConfigRepository.findById(command.getRelicModelId()).orElseThrow(() -> KOSException.of(
                ErrorCode.RELIC_MODEL_NOT_FOUND));
        var relic = new Relic().setCommunityBuilding(communityBuilding).setRelicConfig(relicConfig).setIsListing(false);
        return relicRepository.save(relic);
    }

}

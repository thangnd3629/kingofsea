package com.supergroup.kos.building.domain.service.building;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.supergroup.core.constant.ErrorCode;
import com.supergroup.core.exception.KOSException;
import com.supergroup.kos.building.domain.command.GetCommunityBuildingInfo;
import com.supergroup.kos.building.domain.constant.BuildingName;
import com.supergroup.kos.building.domain.model.building.CommunityBuilding;
import com.supergroup.kos.building.domain.model.config.BaseBuildingConfig;
import com.supergroup.kos.building.domain.model.config.CommunityBuildingConfig;
import com.supergroup.kos.building.domain.model.profile.KosProfile;
import com.supergroup.kos.building.domain.model.relic.Relic;
import com.supergroup.kos.building.domain.repository.persistence.building.BuildingConfigDataSource;
import com.supergroup.kos.building.domain.repository.persistence.building.CommunityBuildingRepository;
import com.supergroup.kos.building.domain.service.config.KosConfigService;
import com.supergroup.kos.building.domain.service.profile.KosProfileService;
import com.supergroup.kos.building.domain.service.technology.TechnologyService;

import lombok.experimental.Delegate;

@Service
public class CommunityBuildingService extends BaseBuildingService {

    @Delegate
    private final CommunityBuildingRepository communityBuildingRepository;
    private final TechnologyService           technologyService;
    private final KosConfigService            kosConfigService;

    @Autowired
    public CommunityBuildingService(KosProfileService kosProfileService,
                                    BuildingConfigDataSource buildingConfigDataSource,
                                    CommunityBuildingRepository communityBuildingRepository,
                                    TechnologyService technologyService,
                                    KosConfigService kosConfigService) {
        super(kosProfileService, buildingConfigDataSource);
        this.communityBuildingRepository = communityBuildingRepository;
        this.technologyService = technologyService;
        this.kosConfigService = kosConfigService;
    }

    public CommunityBuilding getBuildingInfo(GetCommunityBuildingInfo command) {
        var communityBuilding = communityBuildingRepository.findByKosProfileId(command.getKosProfileId())
                                                           .orElseThrow(() -> KOSException.of(ErrorCode.COMMUNITY_BUILDING_IS_NOT_FOUND));

        communityBuilding.validUnlockBuilding(technologyService);

        var mpGained = calcMpFromRelicListings((List<Relic>) communityBuilding.getOwnRelics(), communityBuilding.getKosProfile(), false);
        var communityBuildingConfig = (CommunityBuildingConfig) buildingConfigDataSource.getConfig(BuildingName.COMMUNITY,
                                                                                                   communityBuilding.getLevel());
        communityBuilding.setMpGained(mpGained);
        communityBuilding.setMaxLevelListingRelic(communityBuildingConfig.getMaxLevelListingRelic());
        return communityBuilding;
    }

    public Long calcMpFromRelicListings(List<Relic> ownRelics, KosProfile kosProfile, boolean isIgnoreCheckOccupy) {
        var config = kosConfigService.occupyEffect();
        var mpGained = ownRelics.stream()
                                .filter(Relic::getIsListing)
                                .reduce(0, (partialMpResult, relic) ->
                                        Math.toIntExact(partialMpResult + relic.getRelicConfig().getRelicMpConfig().getMp()), Integer::sum) *
                       (1 + kosProfile.getBonusEffectRelicItemPercent());
        // decrease mp when base occupied
        if (kosProfile.getBase().isOccupied() && !isIgnoreCheckOccupy) {
            return Math.round(mpGained * (1 - config.getDecreaseMp()));
        } else {
            return Math.round(mpGained);
        }
    }

    @Override
    protected BaseBuildingConfig getBuildingConfig(Long level) {
        return buildingConfigDataSource.getConfig(BuildingName.COMMUNITY, level);
    }

}

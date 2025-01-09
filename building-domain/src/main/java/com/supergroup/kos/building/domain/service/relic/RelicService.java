package com.supergroup.kos.building.domain.service.relic;

import java.util.List;
import java.util.Objects;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.supergroup.core.constant.ErrorCode;
import com.supergroup.core.exception.KOSException;
import com.supergroup.kos.building.domain.command.GetCommunityBuildingInfo;
import com.supergroup.kos.building.domain.command.GetMpFromRelicCommand;
import com.supergroup.kos.building.domain.command.GetRelicsCommand;
import com.supergroup.kos.building.domain.command.ListingRelicCommand;
import com.supergroup.kos.building.domain.command.UpdateMpCommand;
import com.supergroup.kos.building.domain.constant.TechnologyCode;
import com.supergroup.kos.building.domain.exception.TechRequirementException;
import com.supergroup.kos.building.domain.model.config.RelicConfig;
import com.supergroup.kos.building.domain.model.relic.Relic;
import com.supergroup.kos.building.domain.model.technology.Technology;
import com.supergroup.kos.building.domain.repository.persistence.relic.RelicConfigRepository;
import com.supergroup.kos.building.domain.repository.persistence.relic.RelicRepository;
import com.supergroup.kos.building.domain.service.building.CommunityBuildingService;
import com.supergroup.kos.building.domain.service.point.PointService;
import com.supergroup.kos.building.domain.service.profile.KosProfileService;
import com.supergroup.kos.building.domain.service.technology.TechnologyService;

import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;

@Service
@RequiredArgsConstructor
public class RelicService {

    @Delegate
    private final RelicRepository relicRepository;
    private final RelicConfigRepository relicConfigRepository;
    private final CommunityBuildingService communityBuildingService;
    private final PointService             pointService;
    private final TechnologyService        technologyService;
    private final KosProfileService        kosProfileService;

    private final static Long MAX_RELIC_LISTING       = 7L;
    private final static Long NUMBER_OF_RELIC_LISTING = 4L;

    public List<Relic> getRelics(GetRelicsCommand command) {
        var isListing = command.getIsListing();
        var kosProfileId = command.getKosProfileId();
        if (Objects.isNull(isListing)) {
            return relicRepository.find(kosProfileId);
        } else {
            return relicRepository.find(kosProfileId, isListing);
        }
    }

    public Long getMpFromRelicListings(GetMpFromRelicCommand command) {
        var relics = relicRepository.findByKosProfileIdAndIsListingTrue(command.getKosProfileId());
        var kosProfile = kosProfileService.findById(command.getKosProfileId())
                                          .orElseThrow(() -> KOSException.of(ErrorCode.KOS_PROFILE_NOT_FOUND));
        return communityBuildingService.calcMpFromRelicListings(relics, kosProfile, command.isIgnoreCheckOccupy());
    }

    public Relic getRelicByIdAndKosProfileId(Long relicId, Long kosProfileId) {
        return relicRepository.findByIdAndCommunityBuilding_KosProfile_Id(relicId, kosProfileId)
                              .orElseThrow(() -> KOSException.of(ErrorCode.RELIC_NOT_FOUND));
    }

    public List<Relic> getRelicByLevelAndKosProfileId(Long level, Long kosProfileId) {
        return relicRepository.findByLevelAndKosProfileId(level, kosProfileId);
    }

    @Transactional
    public void listingRelic(ListingRelicCommand command) {
        var kosProfileId = command.getKosProfileId();
        var relic = getRelicByIdAndKosProfileId(command.getRelicId(), kosProfileId);
        validListingRelic(relic, command.getIsListing(), kosProfileId);
        relicRepository.updateIsListingById(command.getIsListing(), command.getRelicId());
        var multiplier = command.getIsListing().equals(true) ? 1 : -1;
        var diffMp = relic.getRelicConfig().getRelicMpConfig().getMp() * multiplier;
        pointService.updateMp(new UpdateMpCommand().setKosProfileId(kosProfileId).setDiffMp(diffMp));
    }

    private void validListingRelic(Relic relic, Boolean isListing, Long kosProfileId) {
        var communityBuilding = communityBuildingService.getBuildingInfo(new GetCommunityBuildingInfo(kosProfileId));
        if (relic.getIsListing().equals(isListing)) {
            throw KOSException.of(isListing.equals(true) ? ErrorCode.RELIC_IS_LISTING : ErrorCode.RELIC_IS_NOT_LISTING);
        }
        var numberOfListingRelic = (Long) communityBuilding.getOwnRelics().stream().filter(Relic::getIsListing).count();
        var maxListingRelic = communityBuilding.getMaxListingRelic();
        var maxLevelListingRelic = communityBuilding.getMaxLevelListingRelic();
        var relicLevel = relic.getRelicConfig().getRelicMpConfig().getLevel();
        if (isListing.equals(true)) {
            if (numberOfListingRelic >= maxListingRelic) {
                if (maxListingRelic >= MAX_RELIC_LISTING) {
                    throw KOSException.of(ErrorCode.RELIC_SLOT_AVAILABLE);
                } else {
                    Technology technology;
                    if (maxListingRelic < NUMBER_OF_RELIC_LISTING) {
                        technology = technologyService.findByCode(TechnologyCode.SC9);
                    } else {
                        technology = technologyService.findByCode(TechnologyCode.SC18);
                    }
                    throw new TechRequirementException(ErrorCode.RELIC_SLOT_LISTING_IS_FULL, technology);
                }
            }
            if (relicLevel > maxLevelListingRelic) {
                throw KOSException.of(ErrorCode.RELIC_LEVEL_IS_TOO_HIGH);
            }
        }
    }

    public List<Relic> saveAll(List<Relic> relics) {
        return relicRepository.saveAll(relics);
    }

    public Relic save(Relic relic) {
        return relicRepository.save(relic);
    }

    public List<RelicConfig> getAllConfig(Pageable pageable){
        List<RelicConfig> result = relicConfigRepository.findAll();
        return result;
    }
}

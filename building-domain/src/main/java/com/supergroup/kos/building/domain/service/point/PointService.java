package com.supergroup.kos.building.domain.service.point;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.supergroup.core.constant.ErrorCode;
import com.supergroup.core.exception.KOSException;
import com.supergroup.kos.building.domain.command.ConvertGP2TPCommand;
import com.supergroup.kos.building.domain.command.GetMpFromQueenCommand;
import com.supergroup.kos.building.domain.command.GetMpFromRelicCommand;
import com.supergroup.kos.building.domain.command.UpdateMpCommand;
import com.supergroup.kos.building.domain.constant.BuildingName;
import com.supergroup.kos.building.domain.model.config.ResearchBuildingConfig;
import com.supergroup.kos.building.domain.model.point.Point;
import com.supergroup.kos.building.domain.model.profile.KosProfile;
import com.supergroup.kos.building.domain.repository.persistence.building.BuildingConfigDataSource;
import com.supergroup.kos.building.domain.repository.persistence.point.PointRepository;
import com.supergroup.kos.building.domain.service.config.KosConfigService;
import com.supergroup.kos.building.domain.service.profile.KosProfileService;
import com.supergroup.kos.building.domain.service.queen.QueenService;
import com.supergroup.kos.building.domain.service.relic.RelicService;

import lombok.experimental.Delegate;

@Service
public class PointService {

    @Delegate
    private final PointRepository pointRepository;

    private final BuildingConfigDataSource buildingConfigDataSource;
    private final KosConfigService         kosConfigService;
    private final KosProfileService        kosProfileService;
    private final QueenService             queenService;
    private final RelicService             relicService;

    @Autowired
    public PointService(PointRepository pointRepository,
                        BuildingConfigDataSource buildingConfigDataSource,
                        KosConfigService kosConfigService,
                        KosProfileService kosProfileService,
                        @Lazy QueenService queenService,
                        @Lazy RelicService relicService) {
        this.pointRepository = pointRepository;
        this.buildingConfigDataSource = buildingConfigDataSource;
        this.kosConfigService = kosConfigService;
        this.kosProfileService = kosProfileService;
        this.queenService = queenService;
        this.relicService = relicService;
    }

    /**
     * get point details
     * if base was occupied, point will decrease
     * WARNING: do not use this method to get exact point
     */
    public Point getKosPoint(KosProfile kosProfile) {
        var point = kosProfile.getPoint();
        // Hotfix: validate mp point
        var mpQueen = queenService.getMpFromQueens(new GetMpFromQueenCommand().setKosProfile(point.getKosProfile()).setIgnoreCheckOccupy(true));
        var mpRelic = relicService.getMpFromRelicListings(
                new GetMpFromRelicCommand().setKosProfileId(point.getKosProfile().getId()).setIgnoreCheckOccupy(true));
        var mpCastle = kosProfileService.getInitAssetConfig().getMp();
        point.setMpPoint(mpQueen + mpRelic + mpCastle);
        if (kosProfile.getBase().isOccupied()) {
            // decrease mp, because bas was occupied
            if (kosProfile.getBase().isOccupied()) {
                var config = kosConfigService.occupyEffect();
                point.setMpPoint(Math.round(point.getMpPoint() * (1.0 - config.getDecreaseMp())));
            } else {
                point.setMpPoint(point.getMpPoint());
            }
        }
        return point;
    }

    /**
     * convert gp to tp
     */
    public Long convertGloryPointToTechPoint(ConvertGP2TPCommand command) {
        var config = (ResearchBuildingConfig) buildingConfigDataSource.getConfig(BuildingName.RESEARCH,
                                                                                 command.getResearchBuilding().getLevel());
        if (command.getAmount() > command.getPoint().getGpPoint()) {
            throw KOSException.of(ErrorCode.DO_NOT_HAVE_ENOUGH_RESOURCE);
        }
        Double amount = command.getAmount() / config.getConvertRate();
        if (amount < 1.0) {
            throw KOSException.of(ErrorCode.AMOUNT_CONVERT_INVALID);
        }
        Long gpFee = amount.longValue() * config.getConvertRate().longValue();
        command.getPoint().setTpPoint(command.getPoint().getTpPoint() + amount.longValue());
        command.getPoint().setGpPoint(command.getPoint().getGpPoint() - gpFee);
        pointRepository.save(command.getPoint());
        return amount.longValue();
    }

    public void updateMp(UpdateMpCommand command) {
        var point = pointRepository.findByKosProfile_Id(command.getKosProfileId())
                                   .orElseThrow(() -> KOSException.of(ErrorCode.KOS_PROFILE_NOT_FOUND));
        var pointUpdated = point.setMpPoint(point.getMpPoint() + command.getDiffMp());
        var mpQueen = queenService.getMpFromQueens(new GetMpFromQueenCommand().setKosProfile(point.getKosProfile()).setIgnoreCheckOccupy(true));
        var mpRelic = relicService.getMpFromRelicListings(
                new GetMpFromRelicCommand().setKosProfileId(point.getKosProfile().getId()).setIgnoreCheckOccupy(true));
        var mpCastle = kosProfileService.getInitAssetConfig().getMp();
        point.setMpPoint(mpQueen + mpRelic + mpCastle);
        pointRepository.save(pointUpdated);
    }

    public void updateGp(KosProfile kosProfile, Long gloryPoint) {
        var point = kosProfile.getPoint();
        point.setGpPoint(point.getGpPoint() + gloryPoint);
        pointRepository.save(point);
    }
}

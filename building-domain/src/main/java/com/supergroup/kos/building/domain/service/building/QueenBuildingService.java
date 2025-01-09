package com.supergroup.kos.building.domain.service.building;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.supergroup.core.constant.BaseStatus;
import com.supergroup.core.constant.ErrorCode;
import com.supergroup.core.exception.KOSException;
import com.supergroup.kos.building.domain.command.GetOwnQueensCommand;
import com.supergroup.kos.building.domain.command.GetQueenBuildingInfo;
import com.supergroup.kos.building.domain.constant.BuildingName;
import com.supergroup.kos.building.domain.model.config.BaseBuildingConfig;
import com.supergroup.kos.building.domain.model.config.QueenBuildingConfig;
import com.supergroup.kos.building.domain.model.config.QueenConfig;
import com.supergroup.kos.building.domain.model.mining.QueenBuilding;
import com.supergroup.kos.building.domain.model.queen.Queen;
import com.supergroup.kos.building.domain.repository.persistence.building.BuildingConfigDataSource;
import com.supergroup.kos.building.domain.repository.persistence.building.QueenBuildingRepository;
import com.supergroup.kos.building.domain.repository.persistence.queen.QueenConfigDataSource;
import com.supergroup.kos.building.domain.service.profile.KosProfileService;
import com.supergroup.kos.building.domain.service.queen.QueenService;
import com.supergroup.kos.building.domain.service.technology.TechnologyService;

import lombok.experimental.Delegate;

@Service
public class QueenBuildingService extends BaseBuildingService {

    @Delegate
    private final QueenBuildingRepository queenBuildingRepository;
    private final QueenConfigDataSource   queenConfigDataSource;
    private final QueenService            queenService;
    private final TechnologyService       technologyService;

    public QueenBuildingService(@Autowired KosProfileService kosProfileService,
                                @Autowired BuildingConfigDataSource buildingConfigDataSource,
                                @Autowired QueenBuildingRepository queenBuildingRepository,
                                @Autowired QueenConfigDataSource queenConfigDataSource,
                                @Autowired QueenService queenService,
                                @Autowired TechnologyService technologyService) {
        super(kosProfileService, buildingConfigDataSource);
        this.queenBuildingRepository = queenBuildingRepository;
        this.queenConfigDataSource = queenConfigDataSource;
        this.queenService = queenService;
        this.technologyService = technologyService;
    }

    /**
     * Get building info
     */
    public QueenBuilding getBuildingInfo(GetQueenBuildingInfo command) {
        var queenBuilding = queenBuildingRepository.findByKosProfileId(command.getKosProfileId())
                                                   .orElseThrow(() -> KOSException.of(ErrorCode.QUEEN_BUILDING_IS_NOT_FOUND));
        queenBuilding.validUnlockBuilding(technologyService);
        var ownQueens = queenBuilding.getOwnQueens();
        Long mpGained = queenService.calcMpFromQueens((List<Queen>) ownQueens, queenBuilding.getKosProfile(), false);
        queenBuilding.setMpGained(mpGained);
        return queenBuilding;
    }

    public List<Queen> getOwnQueens(GetOwnQueensCommand command) {
        var queenBuilding = queenBuildingRepository.findByKosProfileId(command.getKosProfileId())
                                                   .orElseThrow(() -> KOSException.of(ErrorCode.QUEEN_BUILDING_IS_NOT_FOUND));
        var ownQueens = queenBuilding.getOwnQueens();
        return (List<Queen>) ownQueens;
    }

    @Transactional
    public List<Queen> spinQueen(Integer numberOfSpin, Long kosProfileId) {
        var queenBuilding = getBuildingInfo(new GetQueenBuildingInfo(kosProfileId));
        var buildingConfig = (QueenBuildingConfig) buildingConfigDataSource.getConfig(BuildingName.QUEEN, queenBuilding.getLevel());
        if (numberOfSpin > queenBuilding.getNumberOfQueenCard() || (numberOfSpin != 1 && numberOfSpin != 5)) {
            throw KOSException.of(ErrorCode.NUMBER_OF_QUEEN_CARD_IS_INVALID);
        }
        var maxQueen = buildingConfig.getMaxQueen();
        if (queenBuilding.getOwnQueens().size() >= maxQueen) {
            throw KOSException.of(ErrorCode.NUMBER_OF_QUEEN_IS_MAXIMUM);
        }
        int numberOfSpinReal = numberOfSpin;
        if (maxQueen - queenBuilding.getOwnQueens().size() < 5) {
            numberOfSpinReal = Math.toIntExact(maxQueen - queenBuilding.getOwnQueens().size());
        }

        // get and filter activate queen to spin
        var queenConfigs = queenConfigDataSource.getAll()
                                                .stream()
                                                .filter((e) -> e.getStatus().equals(BaseStatus.ACTIVATED))
                                                .collect(Collectors.toList());

        List<QueenConfig> queenConfigRandoms = new ArrayList<>();
        numberOfSpinReal = Math.min(numberOfSpin, numberOfSpinReal);
        for (var i = 0; i < numberOfSpinReal; i++) {
            queenConfigRandoms.add(randomQueenModal(queenConfigs));
        }
        var queenRandoms = queenConfigRandoms.stream()
                                             .map(queenConfig -> new Queen().setQueenBuilding(queenBuilding)
                                                                            .setQueenConfig(queenConfig))
                                             .collect(Collectors.toList());

        var response = queenService.saveAll(queenRandoms);
        var newNumberOfQueenCard = queenBuilding.getNumberOfQueenCard() - numberOfSpinReal;
        queenBuildingRepository.updateNumberOfQueenCardById(newNumberOfQueenCard, queenBuilding.getId());
        return response;
    }

    private QueenConfig randomQueenModal(List<QueenConfig> queenConfigs) {
        var size = queenConfigs.size();
        Random random = new Random();
        int indexRandom = random.nextInt(size);
        return queenConfigs.get(indexRandom);
    }

    @Override
    protected BaseBuildingConfig getBuildingConfig(Long level) {
        return buildingConfigDataSource.getConfig(BuildingName.QUEEN, level);
    }

}

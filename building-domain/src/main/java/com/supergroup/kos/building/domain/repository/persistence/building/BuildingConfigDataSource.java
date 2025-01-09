package com.supergroup.kos.building.domain.repository.persistence.building;

import java.util.Comparator;
import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import com.supergroup.core.constant.ErrorCode;
import com.supergroup.core.exception.KOSException;
import com.supergroup.kos.building.domain.constant.BuildingName;
import com.supergroup.kos.building.domain.model.config.BaseBuildingConfig;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BuildingConfigDataSource {

    private final ResearchBuildingConfigRepository   researchBuildingConfigRepository;
    private final VaultBuildingConfigRepository      vaultBuildingConfigRepository;
    private final QueenBuildingConfigRepository      queenBuildingConfigRepository;
    private final CastleConfigRepository             castleConfigRepository;
    private final WoodMineConfigRepository           woodMineConfigRepository;
    private final StoneMineConfigRepository          stoneMineConfigRepository;
    private final StorageBuildingConfigRepository    storageBuildingConfigRepository;
    private final CommunityBuildingConfigRepository  communityBuildingConfigRepository;
    private final ArmoryBuildingConfigRepository     armoryBuildingConfigRepository;
    private final ScoutBuildingConfigRepository      scoutBuildingConfigRepository;
    private final CommandBuildingConfigRepository    commandBuildingConfigRepository;
    private final MilitaryBuildingConfigRepository   militaryBuildingConfigRepository;
    private final LighthouseBuildingConfigRepository lightHouseBuildingConfigRepository;

    @Cacheable(cacheNames = "BuildingConfig", key = "#name.name() + ':' + #level.toString()")
    public BaseBuildingConfig getConfig(BuildingName name, Long level) {
        BaseBuildingConfig config;
        switch (name) {
            case RESEARCH:
                config = researchBuildingConfigRepository.findByLevel(level)
                                                         .orElseThrow(() -> KOSException.of(ErrorCode.CONFIG_NOT_FOUND));
                break;
            case CASTLE:
                config = castleConfigRepository.findByLevel(level)
                                               .orElseThrow(() -> KOSException.of(ErrorCode.CONFIG_NOT_FOUND));
                break;
            case WOOD_MINE:
                config = woodMineConfigRepository.findByLevel(level)
                                                 .orElseThrow(() -> KOSException.of(ErrorCode.CONFIG_NOT_FOUND));
                break;
            case STONE_MINE:
                config = stoneMineConfigRepository.findByLevel(level)
                                                  .orElseThrow(() -> KOSException.of(ErrorCode.CONFIG_NOT_FOUND));
                break;
            case STORAGE_WOOD:
            case STORAGE_STONE:
            case STORAGE_GOLD:
                config = storageBuildingConfigRepository.findByNameAndLevel(name, level)
                                                        .orElseThrow(() -> KOSException.of(ErrorCode.CONFIG_NOT_FOUND));
                break;
            case VAULT:
                config = vaultBuildingConfigRepository.findByLevel(level)
                                                      .orElseThrow(() -> KOSException.of(ErrorCode.CONFIG_NOT_FOUND));
                break;
            case QUEEN:
                config = queenBuildingConfigRepository.findByLevel(level)
                                                      .orElseThrow(() -> KOSException.of(ErrorCode.CONFIG_NOT_FOUND));
                break;
            case COMMUNITY:
                config = communityBuildingConfigRepository.findByLevel(level)
                                                          .orElseThrow(() -> KOSException.of(ErrorCode.CONFIG_NOT_FOUND));
                break;
            case COMMAND:
                config = commandBuildingConfigRepository.findByLevel(level)
                                                        .orElseThrow(() -> KOSException.of(ErrorCode.CONFIG_NOT_FOUND));
                break;
            case ARMORY:
                config = armoryBuildingConfigRepository.findByLevel(level)
                                                       .orElseThrow(() -> KOSException.of(ErrorCode.CONFIG_NOT_FOUND));
                break;
            case SCOUT:
                config = scoutBuildingConfigRepository.findByLevel(level)
                                                      .orElseThrow(() -> KOSException.of(ErrorCode.CONFIG_NOT_FOUND));
                break;
            case MILITARY:
                config = militaryBuildingConfigRepository.findByLevel(level)
                                                         .orElseThrow(() -> KOSException.of(ErrorCode.CONFIG_NOT_FOUND));
                break;
            case LIGHTHOUSE:
                config = lightHouseBuildingConfigRepository.findByLevel(level)
                                                           .orElseThrow(() -> KOSException.of(ErrorCode.CONFIG_NOT_FOUND));
                break;
            default:
                throw KOSException.of(ErrorCode.CONFIG_NOT_FOUND);
        }
        return config;
    }

    @Cacheable(cacheNames = "BuildingConfig", key = "#name.name() + ':List'")
    public List<? extends BaseBuildingConfig> getListConfig(BuildingName name) {
        List<? extends BaseBuildingConfig> configs;
        switch (name) {
            case RESEARCH:
                configs = researchBuildingConfigRepository.findAll();
                break;
            case CASTLE:
                configs = castleConfigRepository.findAll();
                break;
            case WOOD_MINE:
                configs = woodMineConfigRepository.findAll();
                break;
            case STONE_MINE:
                configs = stoneMineConfigRepository.findAll();
                break;
            case STORAGE_WOOD:
            case STORAGE_STONE:
            case STORAGE_GOLD:
                configs = storageBuildingConfigRepository.findByName(name);
                break;
            case VAULT:
                configs = vaultBuildingConfigRepository.findAll();
                break;
            case QUEEN:
                configs = queenBuildingConfigRepository.findAll();
                break;
            case COMMUNITY:
                configs = communityBuildingConfigRepository.findAll();
                break;
            case COMMAND:
                configs = commandBuildingConfigRepository.findAll();
                break;
            case ARMORY:
                configs = armoryBuildingConfigRepository.findAll();
                break;
            case SCOUT:
                configs = scoutBuildingConfigRepository.findAll();
                break;
            case MILITARY:
                configs = militaryBuildingConfigRepository.findAll();
                break;
            case LIGHTHOUSE:
                configs = lightHouseBuildingConfigRepository.findAll();
                break;
            default:
                throw KOSException.of(ErrorCode.CONFIG_NOT_FOUND);
        }
        configs.sort(Comparator.comparingLong((BaseBuildingConfig::getLevel)));
        return configs;
    }

}

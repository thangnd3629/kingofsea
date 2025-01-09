package com.supergroup.kos.building.domain.repository.persistence.building;

import org.springframework.stereotype.Component;

import com.supergroup.core.constant.ErrorCode;
import com.supergroup.core.exception.KOSException;
import com.supergroup.kos.building.domain.constant.BuildingName;
import com.supergroup.kos.building.domain.constant.StorageType;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BuildingConfigRepository {

    private final CastleConfigRepository            castleConfigRepository;
    private final ResearchBuildingConfigRepository  researchBuildingConfigRepository;
    private final WoodMineConfigRepository          woodMineConfigRepository;
    private final StoneMineConfigRepository         stoneMineConfigRepository;
    private final StorageBuildingConfigRepository   storageBuildingConfigRepository;
    private final QueenBuildingConfigRepository     queenBuildingConfigRepository;
    private final VaultBuildingConfigRepository     vaultBuildingConfigRepository;
    private final CommunityBuildingConfigRepository communityBuildingConfigRepository;
    private final ArmoryBuildingConfigRepository    armoryBuildingConfigRepository;
    private final CommandBuildingRepository         commandBuildingRepository;
    private final MilitaryBuildingRepository        militaryBuildingRepository;
    private final ScoutBuildingRepository      scoutBuildingRepository;
    private final LighthouseBuildingRepository lightHouseBuildingRepository;

    public Boolean existsByBuildingNameAndLevel(BuildingName name, Long level) {
        switch (name) {
            case CASTLE:
                return castleConfigRepository.existsByLevel(level);
            case RESEARCH:
                return researchBuildingConfigRepository.existsByLevel(level);
            case WOOD_MINE:
                return woodMineConfigRepository.existsByLevel(level);
            case STONE_MINE:
                return stoneMineConfigRepository.existsByLevel(level);
            case QUEEN:
                return queenBuildingConfigRepository.existsByLevel(level);
            case VAULT:
                return vaultBuildingConfigRepository.existsByLevel(level);
            case COMMUNITY:
                return communityBuildingConfigRepository.existsByLevel(level);
            case ARMORY:
                return armoryBuildingConfigRepository.existsByLevel(level);
            case COMMAND:
                return commandBuildingRepository.existsByLevel(level);
            case MILITARY:
                return militaryBuildingRepository.existsByLevel(level);
            case STORAGE_GOLD:
                return storageBuildingConfigRepository.existsByLevelAndType(level, StorageType.GOLD);
            case STORAGE_STONE:
                return storageBuildingConfigRepository.existsByLevelAndType(level, StorageType.STONE);
            case STORAGE_WOOD:
                return storageBuildingConfigRepository.existsByLevelAndType(level, StorageType.WOOD);
            case SCOUT:
                return scoutBuildingRepository.existsByLevel(level);
            case LIGHTHOUSE:
                return lightHouseBuildingRepository.existsByLevel(level);
            default:
                throw KOSException.of(ErrorCode.CONFIG_NOT_FOUND);
        }
    }
}

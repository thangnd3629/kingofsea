package com.supergroup.kos.building.domain.repository.persistence.building;

import org.springframework.stereotype.Component;

import com.supergroup.core.constant.ErrorCode;
import com.supergroup.core.exception.KOSException;
import com.supergroup.kos.building.domain.constant.BuildingName;
import com.supergroup.kos.building.domain.constant.StorageType;
import com.supergroup.kos.building.domain.model.building.ArmoryBuilding;
import com.supergroup.kos.building.domain.model.building.BaseBuilding;
import com.supergroup.kos.building.domain.model.building.CastleBuilding;
import com.supergroup.kos.building.domain.model.building.CommandBuilding;
import com.supergroup.kos.building.domain.model.building.CommunityBuilding;
import com.supergroup.kos.building.domain.model.building.LighthouseBuilding;
import com.supergroup.kos.building.domain.model.building.MilitaryBuilding;
import com.supergroup.kos.building.domain.model.building.StoneMineBuilding;
import com.supergroup.kos.building.domain.model.building.StorageBuilding;
import com.supergroup.kos.building.domain.model.building.VaultBuilding;
import com.supergroup.kos.building.domain.model.building.WoodMineBuilding;
import com.supergroup.kos.building.domain.model.mining.QueenBuilding;
import com.supergroup.kos.building.domain.model.mining.ResearchBuilding;
import com.supergroup.kos.building.domain.model.mining.ScoutBuilding;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BuildingRepository {

    private final CastleBuildingRepository     castleBuildingRepository;
    private final ResearchBuildingRepository   researchBuildingRepository;
    private final WoodMineBuildingRepository   woodMineBuildingRepository;
    private final StoneMineBuildingRepository  stoneMineBuildingRepository;
    private final StorageBuildingRepository    storageBuildingRepository;
    private final QueenBuildingRepository      queenBuildingRepository;
    private final VaultBuildingRepository      vaultBuildingRepository;
    private final CommunityBuildingRepository  communityBuildingRepository;
    private final ArmoryBuildingRepository     armoryBuildingRepository;
    private final ScoutBuildingRepository      scoutBuildingRepository;
    private final CommandBuildingRepository    commandBuildingRepository;
    private final MilitaryBuildingRepository   militaryBuildingRepository;
    private final LighthouseBuildingRepository lightHouseBuildingRepository;

    public void save(BaseBuilding building) {
        if (building instanceof CastleBuilding) {
            castleBuildingRepository.save((CastleBuilding) building);
        } else if (building instanceof ResearchBuilding) {
            researchBuildingRepository.save((ResearchBuilding) building);
        } else if (building instanceof WoodMineBuilding) {
            woodMineBuildingRepository.save((WoodMineBuilding) building);
        } else if (building instanceof StoneMineBuilding) {
            stoneMineBuildingRepository.save((StoneMineBuilding) building);
        } else if (building instanceof StorageBuilding) {
            storageBuildingRepository.save((StorageBuilding) building);
        } else if (building instanceof QueenBuilding) {
            queenBuildingRepository.save((QueenBuilding) building);
        } else if (building instanceof VaultBuilding) {
            vaultBuildingRepository.save((VaultBuilding) building);
        } else if (building instanceof CommunityBuilding) {
            communityBuildingRepository.save((CommunityBuilding) building);
        } else if (building instanceof ArmoryBuilding) {
            armoryBuildingRepository.save((ArmoryBuilding) building);
        } else if (building instanceof ScoutBuilding) {
            scoutBuildingRepository.save((ScoutBuilding) building);
        } else if (building instanceof CommandBuilding) {
            commandBuildingRepository.save((CommandBuilding) building);
        } else if (building instanceof MilitaryBuilding) {
            militaryBuildingRepository.save((MilitaryBuilding) building);
        } else if (building instanceof LighthouseBuilding) {
            lightHouseBuildingRepository.save((LighthouseBuilding) building);
        }
    }

    public BaseBuilding get(BuildingName name, Long kosProfileId) {
        switch (name) {
            case CASTLE:
                return castleBuildingRepository.findByKosProfile_Id(kosProfileId)
                                               .orElseThrow(() -> KOSException.of(ErrorCode.CASTLE_BUILDING_NOT_FOUND));
            case RESEARCH:
                return researchBuildingRepository.findByKosProfileId(kosProfileId)
                                                 .orElseThrow(() -> KOSException.of(ErrorCode.RESEARCH_BUILDING_IS_NOT_FOUND));
            case WOOD_MINE:
                return woodMineBuildingRepository.findByKosProfileId(kosProfileId)
                                                 .orElseThrow(() -> KOSException.of(ErrorCode.WOOD_MINE_BUILDING_IS_NOT_FOUND));
            case STONE_MINE:
                return stoneMineBuildingRepository.findByKosProfileId(kosProfileId)
                                                  .orElseThrow(() -> KOSException.of(ErrorCode.STONE_MINE_BUILDING_IS_NOT_FOUND));
            case VAULT:
                return vaultBuildingRepository.findByKosProfileId(kosProfileId)
                                              .orElseThrow(() -> KOSException.of(ErrorCode.VAULT_BUILDING_IS_NOT_FOUND));
            case QUEEN:
                return queenBuildingRepository.findByKosProfileId(kosProfileId)
                                              .orElseThrow(() -> KOSException.of(ErrorCode.QUEEN_BUILDING_CONFIG_IS_NOT_FOUND));
            case COMMUNITY:
                return communityBuildingRepository.findByKosProfileId(kosProfileId)
                                                  .orElseThrow(() -> KOSException.of(ErrorCode.COMMUNITY_BUILDING_IS_NOT_FOUND));
            case ARMORY:
                return armoryBuildingRepository.findByKosProfileId(kosProfileId)
                                               .orElseThrow(() -> KOSException.of(ErrorCode.ARMORY_BUILDING_IS_NOT_FOUND));
            case STORAGE_GOLD:
                return storageBuildingRepository.findByKosProfileIdAndStorageType(kosProfileId, StorageType.GOLD)
                                                .orElseThrow(() -> KOSException.of(ErrorCode.STORAGE_BUILDING_NOT_FOUND));
            case STORAGE_STONE:
                return storageBuildingRepository.findByKosProfileIdAndStorageType(kosProfileId, StorageType.STONE)
                                                .orElseThrow(() -> KOSException.of(ErrorCode.STORAGE_BUILDING_NOT_FOUND));
            case STORAGE_WOOD:
                return storageBuildingRepository.findByKosProfileIdAndStorageType(kosProfileId, StorageType.WOOD)
                                                .orElseThrow(() -> KOSException.of(ErrorCode.STORAGE_BUILDING_NOT_FOUND));
            case SCOUT:
                return scoutBuildingRepository.findByKosProfile_Id(kosProfileId)
                                              .orElseThrow(() -> KOSException.of(ErrorCode.SCOUT_BUILDING_CONFIG_IS_NOT_FOUND));
            case COMMAND:
                return commandBuildingRepository.findByKosProfile_Id(kosProfileId)
                                                .orElseThrow(() -> KOSException.of(ErrorCode.COMMAND_BUILDING_IS_NOT_FOUND));
            case MILITARY:
                return militaryBuildingRepository.findByKosProfile_Id(kosProfileId)
                                                 .orElseThrow(() -> KOSException.of(ErrorCode.MILITARY_BUILDING_IS_NOT_FOUND));
            case LIGHTHOUSE:
                return lightHouseBuildingRepository.findByKosProfileId(kosProfileId).orElseThrow(
                        () -> KOSException.of(ErrorCode.LIGHTHOUSE_BUILDING_IS_NOT_FOUND));
            default:
                throw KOSException.of(ErrorCode.CONFIG_NOT_FOUND);
        }
    }

}

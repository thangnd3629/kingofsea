package com.supergroup.kos.building.domain.mapper.elements.transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.mapstruct.Mapper;

import com.supergroup.kos.building.domain.constant.seamap.SeaElementType;
import com.supergroup.kos.building.domain.model.config.seamap.BossSeaConfig;
import com.supergroup.kos.building.domain.model.config.seamap.ResourceIslandConfig;
import com.supergroup.kos.building.domain.model.config.seamap.SeaElementConfig;
import com.supergroup.kos.building.domain.model.config.seamap.ShipElementConfig;
import com.supergroup.kos.building.domain.model.config.seamap.UserBaseConfig;
import com.supergroup.kos.building.domain.model.seamap.Anchor;
import com.supergroup.kos.building.domain.model.seamap.BossSea;
import com.supergroup.kos.building.domain.model.seamap.ElementTransactionModel;
import com.supergroup.kos.building.domain.model.seamap.ResourceIsland;
import com.supergroup.kos.building.domain.model.seamap.SeaElement;
import com.supergroup.kos.building.domain.model.seamap.SeaElementConfigTransaction;
import com.supergroup.kos.building.domain.model.seamap.ShipElement;
import com.supergroup.kos.building.domain.model.seamap.UserBase;

@Mapper
public interface SeaMapTransactionMapper {
    default List<ElementTransactionModel> mapToSeaMapRefreshTransaction(List<SeaElement> seaElements) {
        List<ElementTransactionModel> result = new ArrayList<>();
        for (SeaElement seaElement : seaElements) {
            ElementTransactionModel elementTransactionModel = new ElementTransactionModel();
            elementTransactionModel.setId(seaElement.getId())
                                   .setX(seaElement.getX())
                                   .setY(seaElement.getY())
                                   .setElementId(seaElement.getSeaElementConfig().getId())
                                   .setDependentElementId(seaElement.getDependentElementId());
            if (seaElement instanceof UserBase) {
                UserBase userBase = (UserBase) seaElement;
                elementTransactionModel.setType(SeaElementType.USER_BASE.name())
                        .setKosProfileId(Objects.nonNull(userBase.getKosProfile()) ? userBase.getKosProfile().getId(): null)
                        .setName(userBase.getIslandName());
            } else if (seaElement instanceof ResourceIsland) {
                elementTransactionModel.setType(SeaElementType.RESOURCE.name());
            } else if (seaElement instanceof BossSea) {
                elementTransactionModel.setType(SeaElementType.BOSS.name());
            } else if (seaElement instanceof ShipElement) {
                elementTransactionModel.setType(SeaElementType.SHIP.name());
            } else if (seaElement instanceof Anchor) {
                elementTransactionModel.setType(SeaElementType.OCCUPIED_ENEMY_BASE.name());
            } else {

            }
            result.add(elementTransactionModel);
        }
        return result;
    }

    default List<SeaElementConfigTransaction> mapToSeaElementConfigTransaction(List<SeaElementConfig> configs) {
        List<SeaElementConfigTransaction> result = new ArrayList<>();
        for (SeaElementConfig config : configs) {
            SeaElementConfigTransaction seaElementConfigTransaction = new SeaElementConfigTransaction();
            seaElementConfigTransaction.setId(config.getId())
                                       .setOccupied(config.getOccupied())
                                       .setLevel(config.getLevel())
                                       .setName(config.getName())
                                       .setThumbnail(config.getThumbnail());
            if (config instanceof UserBaseConfig) {
                seaElementConfigTransaction.setSeaElementType(SeaElementType.USER_BASE);
            } else if (config instanceof ResourceIslandConfig) {
                ResourceIslandConfig resourceIslandConfig = (ResourceIslandConfig) config;
                seaElementConfigTransaction.setSeaElementType(SeaElementType.RESOURCE)
                                           .setResourceType(resourceIslandConfig.getResourceType())
                                           .setResourceCapacity(resourceIslandConfig.getResourceCapacity())
                                           .setResourceExploitSpeed(resourceIslandConfig.getResourceExploitSpeed());
            } else if (config instanceof BossSeaConfig) {
                BossSeaConfig bossSeaConfig = (BossSeaConfig) config;
                seaElementConfigTransaction.setSeaElementType(SeaElementType.BOSS)
                                           .setBossType(bossSeaConfig.getBossType())
                                           .setBossAtk1(bossSeaConfig.getBossAtk1())
                                           .setBossAtk2(bossSeaConfig.getBossAtk2())
                                           .setBossHp(bossSeaConfig.getBossHp())
                                           .setBossDef1(bossSeaConfig.getBossDef1())
                                           .setBossDef2(bossSeaConfig.getBossDef2())
                                           .setBossDodge(bossSeaConfig.getBossDodge())
                                           .setBossTimeRespawn(bossSeaConfig.getBossTimeRespawn());
            } else if (config instanceof ShipElementConfig) {
                seaElementConfigTransaction.setSeaElementType(SeaElementType.SHIP);
            } else {
            }
            result.add(seaElementConfigTransaction);
        }
        return result;
    }
}

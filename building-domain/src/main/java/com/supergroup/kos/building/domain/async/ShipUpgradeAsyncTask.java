package com.supergroup.kos.building.domain.async;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.supergroup.core.constant.NotificationRenderContentPlaceHolder;
import com.supergroup.kos.building.domain.constant.battle.ShipStatisticType;
import com.supergroup.kos.building.domain.model.config.EscortShipConfig;
import com.supergroup.kos.building.domain.model.config.EscortShipGroupLevelConfig;
import com.supergroup.kos.building.domain.model.config.EscortShipLevelConfig;
import com.supergroup.kos.building.domain.model.config.MotherShipConfig;
import com.supergroup.kos.building.domain.model.config.MotherShipLevelConfig;
import com.supergroup.kos.building.domain.model.config.MotherShipQualityConfig;
import com.supergroup.kos.building.domain.utils.NotificationUtils;
import com.supergroup.kos.notification.domain.model.NotificationTemplateType;
import com.supergroup.kos.notification.domain.service.NotificationTemplateService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ShipUpgradeAsyncTask {
    private final NotificationTemplateService notificationTemplateService;

    public void sendMotherShipUpgradeQualityNotification(Long userId, MotherShipConfig config, MotherShipQualityConfig currentQuality,
                                                         MotherShipQualityConfig nextQuality) {

        try {
            notificationTemplateService.sendByTemplate(userId, NotificationTemplateType.MOTHER_SHIP_QUALITY_UPGRADE_SUCCESSFUL,
                                                       buildShipNewQualityStatParamsMap(config, currentQuality, nextQuality), null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMotherShipUpgradeLevelNotification(Long userId, MotherShipConfig config, MotherShipLevelConfig currentLevel,
                                                       MotherShipLevelConfig nextLevel) {
        try {
            notificationTemplateService.sendByTemplate(userId, NotificationTemplateType.MOTHER_SHIP_LEVEL_UPGRADE_SUCCESSFUL,
                                                       buildShipNewLevelStatParamsMap(config, currentLevel, nextLevel), null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendGuardShipUpgradeQualityNotification(Long userId, String groupName, EscortShipGroupLevelConfig levelConfig,
                                                        Double nextLevelPercentStat) {
        try {
            Map<String, Object> namedParams = new HashMap<>();
            namedParams.put(NotificationRenderContentPlaceHolder.STAT_PERCENT, nextLevelPercentStat * 100);
            namedParams.put(NotificationRenderContentPlaceHolder.GUARD_SHIP_GROUP, groupName.toLowerCase());
            namedParams.put(NotificationRenderContentPlaceHolder.QUALITY, levelConfig.getLevel().name());
            notificationTemplateService.sendByTemplate(userId, NotificationTemplateType.GUARD_SHIP_UPGRADE_QUALITY_SUCCESSFUL, namedParams, null,
                                                       null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendGuardShipUpgradeLevelNotification(Long userId, EscortShipConfig config, EscortShipLevelConfig currentLevelPercentStat,
                                                      EscortShipLevelConfig nextLevelPercentStat) {
        try {
            notificationTemplateService.sendByTemplate(userId, NotificationTemplateType.GUARD_SHIP_UPGRADE_LEVEL_SUCCESSFUL,
                                                       buildGuardShipNewLevelStatParamsMap(config, nextLevelPercentStat.getLevel(),
                                                                                           currentLevelPercentStat.getPercentStat(),
                                                                                           nextLevelPercentStat.getPercentStat()), null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Map<String, Object> buildGuardShipNewLevelStatParamsMap(EscortShipConfig config, Long nextLevel, Double currentLevelPercentStat,
                                                                    Double nextLevelPercentStat) {
        Map<String, Object> namedParams = new HashMap<>();
        namedParams.put(NotificationRenderContentPlaceHolder.GUARD_SHIP_NAME, config.getName());
        namedParams.put(NotificationRenderContentPlaceHolder.LEVEL, nextLevel);
        namedParams.put(ShipStatisticType.ATK1.toString(), (long) (config.getAtk1() * nextLevelPercentStat));
        namedParams.put(ShipStatisticType.ATK2.toString(), (long) (config.getAtk2() * nextLevelPercentStat));
        namedParams.put(ShipStatisticType.DEF1.toString(), (long) (config.getDef1() * nextLevelPercentStat));
        namedParams.put(ShipStatisticType.DEF2.toString(), (long) (config.getDef2() * nextLevelPercentStat));
        namedParams.put(ShipStatisticType.HP.toString(), (long) (config.getHp() * nextLevelPercentStat));
        namedParams.put(ShipStatisticType.DODGE.toString(), (long) (config.getAtk1() * nextLevelPercentStat));
        namedParams.put(ShipStatisticType.TONNAGE.toString(), (long) (config.getAtk1() * nextLevelPercentStat));

        namedParams.put(ShipStatisticType.ATK1.toString() + NotificationRenderContentPlaceHolder.GROWTH_POSTFIX,
                        NotificationUtils.getGrowthPercent(config.getAtk1(), currentLevelPercentStat, nextLevelPercentStat));
        namedParams.put(ShipStatisticType.ATK2.toString() + NotificationRenderContentPlaceHolder.GROWTH_POSTFIX,
                        NotificationUtils.getGrowthPercent(config.getAtk2(), currentLevelPercentStat, nextLevelPercentStat));
        namedParams.put(ShipStatisticType.DEF1.toString() + NotificationRenderContentPlaceHolder.GROWTH_POSTFIX,
                        NotificationUtils.getGrowthPercent(config.getDef1(), currentLevelPercentStat, nextLevelPercentStat));
        namedParams.put(ShipStatisticType.DEF2.toString() + NotificationRenderContentPlaceHolder.GROWTH_POSTFIX,
                        NotificationUtils.getGrowthPercent(config.getDef2(), currentLevelPercentStat, nextLevelPercentStat));
        namedParams.put(ShipStatisticType.HP.toString() + NotificationRenderContentPlaceHolder.GROWTH_POSTFIX,
                        NotificationUtils.getGrowthPercent(config.getHp(), currentLevelPercentStat, nextLevelPercentStat));
        namedParams.put(ShipStatisticType.DODGE.toString() + NotificationRenderContentPlaceHolder.GROWTH_POSTFIX,
                        NotificationUtils.getGrowthPercent(config.getDodge(), currentLevelPercentStat, nextLevelPercentStat));

        return namedParams;
    }

    private Map<String, Object> buildShipNewLevelStatParamsMap(MotherShipConfig config, MotherShipLevelConfig currentLevel,
                                                               MotherShipLevelConfig nextLevel) {
        Map<String, Object> namedParams = new HashMap<>();
        namedParams.put(NotificationRenderContentPlaceHolder.MOTHERSHIP_NAME, config.getName());
        namedParams.put(NotificationRenderContentPlaceHolder.LEVEL, nextLevel.getLevel());
        Double nextLevelPercentStat = nextLevel.getPercentStat();
        Double currentLevelPercentStat = currentLevel.getPercentStat();
        namedParams.put(ShipStatisticType.ATK1.toString(), (long) (config.getAtk1() * nextLevelPercentStat));
        namedParams.put(ShipStatisticType.ATK2.toString(), (long) (config.getAtk2() * nextLevelPercentStat));
        namedParams.put(ShipStatisticType.DEF1.toString(), (long) (config.getDef1() * nextLevelPercentStat));
        namedParams.put(ShipStatisticType.DEF2.toString(), (long) (config.getDef2() * nextLevelPercentStat));
        namedParams.put(ShipStatisticType.HP.toString(), (long) (config.getHp() * nextLevelPercentStat));
        namedParams.put(ShipStatisticType.DODGE.toString(), (long) (config.getAtk1() * nextLevelPercentStat));
        namedParams.put(ShipStatisticType.TONNAGE.toString(), (long) (config.getAtk1() * nextLevelPercentStat));
        namedParams.put(ShipStatisticType.CMD.toString(), (long) (config.getCmd() * nextLevelPercentStat));
        namedParams.put(ShipStatisticType.SPEED.toString(), (long) (config.getSpeed() * nextLevelPercentStat));
        namedParams.put(ShipStatisticType.ATK1.toString() + NotificationRenderContentPlaceHolder.GROWTH_POSTFIX,
                        NotificationUtils.getGrowthPercent(config.getAtk1(), currentLevelPercentStat, nextLevelPercentStat));
        namedParams.put(ShipStatisticType.ATK2.toString() + NotificationRenderContentPlaceHolder.GROWTH_POSTFIX,
                        NotificationUtils.getGrowthPercent(config.getAtk2(), currentLevelPercentStat, nextLevelPercentStat));
        namedParams.put(ShipStatisticType.DEF1.toString() + NotificationRenderContentPlaceHolder.GROWTH_POSTFIX,
                        NotificationUtils.getGrowthPercent(config.getDef1(), currentLevelPercentStat, nextLevelPercentStat));
        namedParams.put(ShipStatisticType.DEF2.toString() + NotificationRenderContentPlaceHolder.GROWTH_POSTFIX,
                        NotificationUtils.getGrowthPercent(config.getDef2(), currentLevelPercentStat, nextLevelPercentStat));
        namedParams.put(ShipStatisticType.HP.toString() + NotificationRenderContentPlaceHolder.GROWTH_POSTFIX,
                        NotificationUtils.getGrowthPercent(config.getHp(), currentLevelPercentStat, nextLevelPercentStat));
        namedParams.put(ShipStatisticType.DODGE.toString() + NotificationRenderContentPlaceHolder.GROWTH_POSTFIX,
                        NotificationUtils.getGrowthPercent(config.getDodge(), currentLevelPercentStat, nextLevelPercentStat));
        namedParams.put(ShipStatisticType.TONNAGE.toString() + NotificationRenderContentPlaceHolder.GROWTH_POSTFIX,
                        NotificationUtils.getGrowthPercent(config.getTng(), currentLevelPercentStat, nextLevelPercentStat));
        namedParams.put(ShipStatisticType.CMD.toString() + NotificationRenderContentPlaceHolder.GROWTH_POSTFIX,
                        NotificationUtils.getGrowthPercent(config.getCmd(), currentLevelPercentStat, nextLevelPercentStat));
        namedParams.put(ShipStatisticType.SPEED.toString() + NotificationRenderContentPlaceHolder.GROWTH_POSTFIX,
                        NotificationUtils.getGrowthPercent(config.getSpeed(), currentLevelPercentStat, nextLevelPercentStat));
        return namedParams;
    }

    private Map<String, Object> buildShipNewQualityStatParamsMap(MotherShipConfig config, MotherShipQualityConfig currentQuality,
                                                                 MotherShipQualityConfig nextQuality) {
        Map<String, Object> namedParams = new HashMap<>();
        namedParams.put(NotificationRenderContentPlaceHolder.MOTHERSHIP_NAME, config.getName());
        namedParams.put(NotificationRenderContentPlaceHolder.QUALITY, nextQuality.getQuality().name());
        Double nextQualityPercentStat = nextQuality.getPercentStat();
        Double currentQualityPercentStat = currentQuality.getPercentStat();
        namedParams.put(ShipStatisticType.ATK1.toString(), (long) (config.getAtk1() * nextQualityPercentStat));
        namedParams.put(ShipStatisticType.ATK2.toString(), (long) (config.getAtk2() * nextQualityPercentStat));
        namedParams.put(ShipStatisticType.DEF1.toString(), (long) (config.getDef1() * nextQualityPercentStat));
        namedParams.put(ShipStatisticType.DEF2.toString(), (long) (config.getDef2() * nextQualityPercentStat));
        namedParams.put(ShipStatisticType.HP.toString(), (long) (config.getHp() * nextQualityPercentStat));
        namedParams.put(ShipStatisticType.DODGE.toString(), (long) (config.getAtk1() * nextQualityPercentStat));
        namedParams.put(ShipStatisticType.TONNAGE.toString(), (long) (config.getAtk1() * nextQualityPercentStat));
        namedParams.put(ShipStatisticType.CMD.toString(), (long) (config.getCmd() * nextQualityPercentStat));
        namedParams.put(ShipStatisticType.SPEED.toString(), (long) (config.getSpeed() * nextQualityPercentStat));
        namedParams.put(ShipStatisticType.ATK1.toString() + NotificationRenderContentPlaceHolder.GROWTH_POSTFIX,
                        NotificationUtils.getGrowthPercent(config.getAtk1(), currentQualityPercentStat, nextQualityPercentStat));
        namedParams.put(ShipStatisticType.ATK2.toString() + NotificationRenderContentPlaceHolder.GROWTH_POSTFIX,
                        NotificationUtils.getGrowthPercent(config.getAtk2(), currentQualityPercentStat, nextQualityPercentStat));
        namedParams.put(ShipStatisticType.DEF1.toString() + NotificationRenderContentPlaceHolder.GROWTH_POSTFIX,
                        NotificationUtils.getGrowthPercent(config.getDef1(), currentQualityPercentStat, nextQualityPercentStat));
        namedParams.put(ShipStatisticType.DEF2.toString() + NotificationRenderContentPlaceHolder.GROWTH_POSTFIX,
                        NotificationUtils.getGrowthPercent(config.getDef2(), currentQualityPercentStat, nextQualityPercentStat));
        namedParams.put(ShipStatisticType.HP.toString() + NotificationRenderContentPlaceHolder.GROWTH_POSTFIX,
                        NotificationUtils.getGrowthPercent(config.getHp(), currentQualityPercentStat, nextQualityPercentStat));
        namedParams.put(ShipStatisticType.DODGE.toString() + NotificationRenderContentPlaceHolder.GROWTH_POSTFIX,
                        NotificationUtils.getGrowthPercent(config.getDodge(), currentQualityPercentStat, nextQualityPercentStat));
        namedParams.put(ShipStatisticType.TONNAGE.toString() + NotificationRenderContentPlaceHolder.GROWTH_POSTFIX,
                        NotificationUtils.getGrowthPercent(config.getTng(), currentQualityPercentStat, nextQualityPercentStat));
        namedParams.put(ShipStatisticType.CMD.toString() + NotificationRenderContentPlaceHolder.GROWTH_POSTFIX,
                        NotificationUtils.getGrowthPercent(config.getCmd(), currentQualityPercentStat, nextQualityPercentStat));
        namedParams.put(ShipStatisticType.SPEED.toString() + NotificationRenderContentPlaceHolder.GROWTH_POSTFIX,
                        NotificationUtils.getGrowthPercent(config.getSpeed(), currentQualityPercentStat, nextQualityPercentStat));
        return namedParams;
    }
}

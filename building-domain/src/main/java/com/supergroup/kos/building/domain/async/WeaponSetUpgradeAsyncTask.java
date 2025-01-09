package com.supergroup.kos.building.domain.async;

import static com.supergroup.core.constant.NotificationRenderContentPlaceHolder.GOLD;
import static com.supergroup.core.constant.NotificationRenderContentPlaceHolder.GROWTH_POSTFIX;
import static com.supergroup.core.constant.NotificationRenderContentPlaceHolder.WEAPON_SET_NAME;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.supergroup.kos.building.domain.constant.WeaponStat;
import com.supergroup.kos.building.domain.model.config.WeaponSetConfig;
import com.supergroup.kos.building.domain.model.config.WeaponSetLevelConfig;
import com.supergroup.kos.building.domain.model.weapon.Weapon;
import com.supergroup.kos.building.domain.model.weapon.WeaponSet;
import com.supergroup.kos.notification.domain.constant.RenderSection;
import com.supergroup.kos.notification.domain.constant.RenderSection.RenderContent;
import com.supergroup.kos.notification.domain.constant.RenderSection.Style;
import com.supergroup.kos.notification.domain.model.NotificationTemplateType;
import com.supergroup.kos.notification.domain.service.NotificationTemplateService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class WeaponSetUpgradeAsyncTask {
    private final NotificationTemplateService notificationTemplateService;

    public void sendUpgradeQualityNotification(Long userId, WeaponSetLevelConfig currentLevel, WeaponSetLevelConfig nextLevel,
                                               WeaponSetConfig config) {
        try {
            Map<String, Object> params = new HashMap<>();
            WeaponStat type = config.getStat_type();
            Double currentLevelStat = config.getStat() * currentLevel.getPercentStat();
            Double nextLevelStat = config.getStat() * nextLevel.getPercentStat();
            params.put(WEAPON_SET_NAME, config.getName());
            params.put(type.toString(), config.getStat() * currentLevel.getPercentStat());
            params.put(type + GROWTH_POSTFIX, (nextLevelStat - currentLevelStat) * 100 / currentLevelStat);
            notificationTemplateService.sendByTemplate(userId, NotificationTemplateType.WEAPON_SET_QUALITY_UPGRADE, params, null, null);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void sendWeaponCraftSuccessNotification(Long userId, Long cost, Collection<Weapon> components, WeaponSet result) {
        try {
            List<RenderSection> renderSections = new ArrayList<>();
            Map<String, Object> namedParams = new HashMap<>();
            namedParams.put(GOLD, cost);
            namedParams.put(WEAPON_SET_NAME, result.getWeaponSetConfig().getName());
            RenderSection weaponIncluded = new RenderSection();
            weaponIncluded.setSection("Weapon included");
            weaponIncluded.setStyle(Style.THUMBNAIL);
            for (Weapon weapon : components) {
                weaponIncluded.getContent().add(
                        new RenderContent().setThumbnail(weapon.getWeaponConfig().getThumbnail()).setTitle(weapon.getWeaponConfig().getName()));
            }
            RenderSection weaponSetResult = new RenderSection();
            weaponSetResult.setSection("Weapon set");
            weaponSetResult.setStyle(Style.THUMBNAIL);
            weaponSetResult.getContent().add(
                    new RenderContent().setTitle(result.getWeaponSetConfig().getName()).setThumbnail(result.getWeaponSetConfig().getThumbnail()));
            renderSections.addAll(
                    List.of(weaponIncluded, weaponSetResult));
            notificationTemplateService.sendByTemplate(userId, NotificationTemplateType.WEAPON_SET_CRAFTING_SUCCESSFUL, namedParams, null,
                                                       renderSections);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendWeaponCraftFailNotification(Long userId, Long cost, Collection<Weapon> components, Collection<Weapon> lostComponents,
                                                WeaponSetConfig result) {
        try {
            List<RenderSection> renderSections = new ArrayList<>();
            Map<String, Object> namedParams = new HashMap<>();
            namedParams.put(GOLD, cost);
            namedParams.put(WEAPON_SET_NAME, result.getName());
            RenderSection weaponIncluded = new RenderSection();
            weaponIncluded.setSection("Weapon included");
            weaponIncluded.setStyle(Style.THUMBNAIL);
            for (Weapon weapon : components) {
                weaponIncluded.getContent().add(
                        new RenderContent().setThumbnail(weapon.getWeaponConfig().getThumbnail()).setTitle(weapon.getWeaponConfig().getName()));
            }
            RenderSection weaponLost = new RenderSection();
            weaponLost.setSection("Weapon lost");
            for (Weapon weapon : lostComponents) {
                weaponLost.getContent().add(
                        new RenderContent().setThumbnail(weapon.getWeaponConfig().getThumbnail()).setTitle(weapon.getWeaponConfig().getName()));
            }
            weaponLost.setStyle(Style.THUMBNAIL);
            renderSections.addAll(
                    List.of(weaponIncluded, weaponLost));
            notificationTemplateService.sendByTemplate(userId, NotificationTemplateType.WEAPON_SET_CRAFTING_UNSUCCESSFUL, namedParams, null,
                                                       renderSections);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

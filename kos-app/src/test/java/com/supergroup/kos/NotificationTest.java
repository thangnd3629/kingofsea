package com.supergroup.kos;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.supergroup.kos.building.domain.async.AssetsServiceAsyncTask;
import com.supergroup.kos.building.domain.async.ScoutServiceAsyncTask;
import com.supergroup.kos.building.domain.async.WeaponSetUpgradeAsyncTask;
import com.supergroup.kos.building.domain.model.config.WeaponSetLevelConfig;
import com.supergroup.kos.building.domain.service.weapon.WeaponSetConfigService;

@SpringBootTest
public class NotificationTest {
    @Autowired
    private AssetsServiceAsyncTask assetsServiceAsyncTask;
    @Autowired
    private WeaponSetUpgradeAsyncTask weaponSetUpgradeAsyncTask;
    @Autowired
    private WeaponSetConfigService weaponSetConfigService;
    @Autowired
    private ScoutServiceAsyncTask scoutServiceAsyncTask;
    @Test
    public void test(){
        weaponSetUpgradeAsyncTask.sendUpgradeQualityNotification(82L, new WeaponSetLevelConfig().setPercentStat(10.), new WeaponSetLevelConfig().setPercentStat(20.), weaponSetConfigService.getWeaponSetConfigById(4L));
    }
}

package com.supergroup.kos.building.domain.service.config;

import java.util.Objects;

import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.supergroup.core.constant.ConfigKey;
import com.supergroup.core.model.Config;
import com.supergroup.core.provider.ConfigProvider;
import com.supergroup.kos.building.domain.model.battle.OccupyEffect;
import com.supergroup.kos.building.domain.model.config.KosFrequencyConfig;
import com.supergroup.kos.building.domain.model.config.battle.BattleRewardConfig;
import com.supergroup.kos.building.domain.model.config.battle.BattleRewardItem;
import com.supergroup.kos.building.domain.model.config.battle.BattleTimeConfig;
import com.supergroup.kos.building.domain.model.config.battle.BattlefieldDamageConfig;
import com.supergroup.kos.building.domain.model.config.battle.DefBattleConfig;
import com.supergroup.kos.building.domain.model.config.seamap.SeaMapConfig;
import com.supergroup.kos.building.domain.model.config.seamap.SeaMapRefreshConfig;
import com.supergroup.kos.building.domain.model.config.seamap.SpeedSoldierConfig;
import com.supergroup.kos.building.domain.model.config.seamap.ZoneSeaConfig;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class KosConfigService {
    private final ConfigProvider configProvider;
    private final Gson             gson;

    public KosFrequencyConfig getFrequencyConfig() {
        Config config = configProvider.getConfig(ConfigKey.FREQUENCY_ASSET);
        return gson.fromJson(config.getValue(), KosFrequencyConfig.class);
    }

    public SeaMapConfig getSeaMapConfig() {
        Config config = configProvider.getConfig(ConfigKey.SEA_MAP);
        return gson.fromJson(config.getValue(), SeaMapConfig.class);
    }

    public ZoneSeaConfig getZoneSeaConfig() {
        Config config = configProvider.getConfig(ConfigKey.ZONE_SEA);
        return gson.fromJson(config.getValue(), ZoneSeaConfig.class);
    }

    public SpeedSoldierConfig getSpeedSoldierConfig() {
        Config config = configProvider.getConfig(ConfigKey.SPEED_SOLDIER_CONFIG);
        return gson.fromJson(config.getValue(), SpeedSoldierConfig.class);
    }

    public SeaMapRefreshConfig getSeaMapRefreshConfig() {
        Config config = configProvider.getConfig(ConfigKey.SEA_MAP_REFRESH);
        return gson.fromJson(config.getValue(), SeaMapRefreshConfig.class);
    }

    public BattlefieldDamageConfig getBattleFieldDamageConfig() {
        Config config = configProvider.getConfig(ConfigKey.BATTLE_FIELD_DAMAGE_CONFIG);
        return gson.fromJson(config.getValue(), BattlefieldDamageConfig.class);
    }

    public BattleRewardConfig getBattleRewardConfig() {
        Config config = configProvider.getConfig(ConfigKey.BATTLE_REWARD_CONFIG);
        var value = gson.fromJson(config.getValue(), BattleRewardConfig.class);
        for (BattleRewardItem item : value.getItems()) {
            if (Objects.isNull(item.getStart())) {item.setStart(Long.MIN_VALUE);}
            if (Objects.isNull(item.getEnd())) {item.setEnd(Long.MAX_VALUE);}
        }
        return value;
    }

    public BattleTimeConfig getBattleTimeConfig() {
        Config config = configProvider.getConfig(ConfigKey.BATTLE_TIME_CONFIG);
        return gson.fromJson(config.getValue(), BattleTimeConfig.class);
    }

    public DefBattleConfig getDefBattleConfig() {
        Config config = configProvider.getConfig(ConfigKey.DEF_BATTLE_CONFIG);
        return gson.fromJson(config.getValue(), DefBattleConfig.class);
    }

    public OccupyEffect occupyEffect() {
        Config config = configProvider.getConfig(ConfigKey.OCCUPY_EFFECT_CONFIG);
        return gson.fromJson(config.getValue(), OccupyEffect.class);
    }
    public Double getTaxOnOccupiedBase(){
        Config config = configProvider.getConfig(ConfigKey.TAX_ON_OCCUPIED_BASE);
        return gson.fromJson(config.getValue(), Double.class);
    }

}

package com.supergroup.kos;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.supergroup.auth.domain.model.User;
import com.supergroup.auth.domain.service.UserService;
import com.supergroup.core.constant.ConfigKey;
import com.supergroup.core.model.Config;
import com.supergroup.core.service.ConfigService;
import com.supergroup.kos.building.domain.model.config.seamap.SeaElementConfig;
import com.supergroup.kos.building.domain.model.config.seamap.SeaMapConfig;
import com.supergroup.kos.building.domain.model.profile.KosProfile;
import com.supergroup.kos.building.domain.repository.persistence.seamap.SeaElementConfigRepository;
import com.supergroup.kos.building.domain.service.profile.KosProfileService;
import com.supergroup.kos.building.domain.service.seamap.SeaElementService;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
@Service
public class SetUpDataTest {
    private final UserService       userService;
    private final KosProfileService kosProfileService;
    private final ConfigService     configService;
    private final SeaElementService          seaElementService;
    private final SeaElementConfigRepository seaElementConfigRepository;

    public User createUser() {
        User user = new User().setPassword("test").setEmail("test").setOriginEmail("test");
        Optional<User> optional = userService.save(user);
        return optional.get();
    }

    public KosProfile createKosProfile(User user){
        KosProfile kosProfile = new KosProfile().setUser(user);
        return kosProfileService.saveProfile(kosProfile);
    }

    public void deleteAllConfig() {
        configService.deleteAll();
    }

    public SeaMapConfig createSeaMapConfig(SeaMapConfig seaMapConfig) {
        configService.save(new Config().setValue(new Gson().toJson(seaMapConfig)).setKey(ConfigKey.SEA_MAP));
        return seaMapConfig;
    }

//    public ZoneSeaConfig createSeaMapConfig(ZoneSeaConfig zoneSeaConfig) {
//        configService.save(new Config().setValue(new Gson().toJson(zoneSeaConfig)).setKey(ConfigKey.ZONE_SEA));
//        return zoneSeaConfig;
//    }
//
//    public UserBase createUserBaseActive(UserBase userBase) {
//        seaElementService.deleteAll();
//        seaElementService.save(userBase);
//        return userBase;
//    }

    public List<SeaElementConfig> createListSeaElementConfig(List<SeaElementConfig> list) {
        seaElementConfigRepository.deleteAll();
        seaElementConfigRepository.save(list);
        return list;
    }
}

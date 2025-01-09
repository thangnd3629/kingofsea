package com.supergroup.kos;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ResourceUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.supergroup.kos.building.domain.constant.BuildingName;
import com.supergroup.kos.building.domain.constant.seamap.BossSeaStatus;
import com.supergroup.kos.building.domain.constant.seamap.SeaElementType;
import com.supergroup.kos.building.domain.model.building.CastleBuilding;
import com.supergroup.kos.building.domain.model.config.seamap.BossSeaConfig;
import com.supergroup.kos.building.domain.model.config.seamap.ResourceIslandConfig;
import com.supergroup.kos.building.domain.model.config.seamap.SeaElementConfig;
import com.supergroup.kos.building.domain.model.config.seamap.UserBaseConfig;
import com.supergroup.kos.building.domain.model.profile.KosProfile;
import com.supergroup.kos.building.domain.model.seamap.BossSea;
import com.supergroup.kos.building.domain.model.seamap.ResourceIsland;
import com.supergroup.kos.building.domain.model.seamap.SeaElement;
import com.supergroup.kos.building.domain.model.seamap.UserBase;
import com.supergroup.kos.building.domain.repository.persistence.building.CastleBuildingRepository;
import com.supergroup.kos.building.domain.repository.persistence.profile.KosProfileRepository;
import com.supergroup.kos.building.domain.repository.persistence.seamap.SeaElementConfigRepository;
import com.supergroup.kos.building.domain.repository.persistence.seamap.SeaElementRepository;

@SpringBootTest(properties = { "seamap.parcel-size=100", "seamap.size=2000" })
@ActiveProfiles("test")
@TestMethodOrder(OrderAnnotation.class)
@Transactional
public class SeaElementRepositoryTest {

    @Autowired
    private SeaElementRepository<SeaElement>             seaElementRepository;
    @Autowired
    private ObjectMapper                                 objectMapper;
    @Autowired
    private CastleBuildingRepository                     castleBuildingRepository;
    @Autowired
    private KosProfileRepository                         kosProfileRepository;
    @Autowired
    private SeaElementConfigRepository<SeaElementConfig> seaElementConfigRepository;

    @Test
    public void test_delete_by_types() throws IOException {
        seaElementRepository.deleteAll();
        // setup data for map
        var dataBoss = FileUtils.readFileToString(ResourceUtils.getFile("classpath:test/map/v1/map_data_boss.json"));
        var dataResource = FileUtils.readFileToString(ResourceUtils.getFile("classpath:test/map/v1/map_data_resource.json"));
        var dataUserBase = FileUtils.readFileToString(ResourceUtils.getFile("classpath:test/map/v1/map_data_user_base.json"));

        var data = new ArrayList<SeaElement>();

        var bosses = objectMapper.readValue(dataBoss, new TypeReference<List<BossSea>>() {});
        var bossConfig = new BossSeaConfig().setBossAtk1(100L);
        bossConfig = seaElementConfigRepository.save(bossConfig);
        for (BossSea boss : bosses) {
            boss.setStatus(BossSeaStatus.FIGHTING);
            boss.setHpLost(100L);
            boss.setActive(true);
            boss.setSeaElementConfig(bossConfig);
        }
        var resources = objectMapper.readValue(dataResource, new TypeReference<List<ResourceIsland>>() {});
        var resourceConfig = new ResourceIslandConfig().setResourceCapacity(100.0D).setId(2L);
        resourceConfig = seaElementConfigRepository.save(resourceConfig);
        for (var resource : resources) {
            resource.setMined(100.0);
            resource.setActive(true);
            resource.setSeaElementConfig(resourceConfig);
        }

        var kosProfile = new KosProfile();
        kosProfile = kosProfileRepository.save(kosProfile);
        var castleBuilding = new CastleBuilding();
        castleBuilding.setLevel(1L)
                      .setName(BuildingName.CASTLE)
                      .setKosProfile(kosProfile);
        castleBuildingRepository.save(castleBuilding);

        var userBases = objectMapper.readValue(dataUserBase, new TypeReference<List<UserBase>>() {});
        var userBaseConfig = new UserBaseConfig().setName("Oc dao than tien").setId(3L);
        userBaseConfig = seaElementConfigRepository.save(userBaseConfig);
        for (var base : userBases) {
            base.setIslandName("Test island");
            base.setActive(true);
            base.setKosProfile(kosProfile);
            base.setSeaElementConfig(userBaseConfig);
        }

        data.addAll(bosses);
        data.addAll(resources);
        data.addAll(userBases);

        seaElementRepository.saveAll(data);

        seaElementRepository.deleteByListType(List.of(SeaElementType.BOSS.name(), SeaElementType.RESOURCE.name()));
        var list = seaElementRepository.findAll();
        Assertions.assertEquals(2, list.size());
    }
}

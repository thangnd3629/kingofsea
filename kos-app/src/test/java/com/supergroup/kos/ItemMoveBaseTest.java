package com.supergroup.kos;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.supergroup.auth.domain.model.User;
import com.supergroup.auth.domain.repository.persistence.UserRepository;
import com.supergroup.kos.building.domain.command.UseItemCommand;
import com.supergroup.kos.building.domain.constant.battle.BattleStatus;
import com.supergroup.kos.building.domain.constant.item.ItemId;
import com.supergroup.kos.building.domain.dto.seamap.UserBaseCache;
import com.supergroup.kos.building.domain.model.asset.Assets;
import com.supergroup.kos.building.domain.model.battle.Battle;
import com.supergroup.kos.building.domain.model.battle.BattleProfile;
import com.supergroup.kos.building.domain.model.building.CastleBuilding;
import com.supergroup.kos.building.domain.model.item.UserItem;
import com.supergroup.kos.building.domain.model.profile.KosProfile;
import com.supergroup.kos.building.domain.model.seamap.Coordinates;
import com.supergroup.kos.building.domain.model.seamap.UserBase;
import com.supergroup.kos.building.domain.repository.persistence.asset.AssetsRepository;
import com.supergroup.kos.building.domain.repository.persistence.battle.BattleRepository;
import com.supergroup.kos.building.domain.repository.persistence.building.CastleBuildingRepository;
import com.supergroup.kos.building.domain.repository.persistence.item.ItemEffectRepository;
import com.supergroup.kos.building.domain.repository.persistence.item.ItemRepository;
import com.supergroup.kos.building.domain.repository.persistence.item.UserItemRepository;
import com.supergroup.kos.building.domain.repository.persistence.profile.KosProfileRepository;
import com.supergroup.kos.building.domain.repository.persistence.seamap.UserBaseRepository;
import com.supergroup.kos.building.domain.service.seamap.SeaElementService;
import com.supergroup.kos.building.domain.service.seamap.UserBaseService;
import com.supergroup.kos.building.domain.service.seamap.item.ItemService;

@SpringBootTest(properties = { "seamap.parcel-size=40", "seamap.size=2000" })
@ActiveProfiles("test")
@TestMethodOrder(OrderAnnotation.class)
@Transactional
@Rollback
public class ItemMoveBaseTest {

    @Autowired
    private KosProfileRepository     kosProfileRepository;
    @Autowired
    private UserBaseRepository       userBaseRepository;
    @Autowired
    private UserBaseService          userBaseService;
    @Autowired
    private SeaElementService        seaElementService;
    @Autowired
    private CastleBuildingRepository castleBuildingRepository;
    @Autowired
    private ItemRepository           itemRepository;
    @Autowired
    private ItemEffectRepository     itemEffectRepository;
    @Autowired
    private UserRepository           userRepository;
    @Autowired
    private AssetsRepository         assetsRepository;
    @Autowired
    private UserItemRepository       userItemRepository;
    @Autowired
    private ItemService              itemService;
    @Autowired
    private BattleRepository         battleRepository;

    @Test
    public void test_user_item_wa_11() throws JsonProcessingException {
        // init base
        var user = new User();
        user.setEmail("test@gmail.com")
            .setOriginEmail("test@gmail.com")
            .setPassword("123123123");
        user = userRepository.save(user);
        var kosProfile = new KosProfile();
        kosProfile.setUser(user);
        kosProfile = kosProfileRepository.save(kosProfile);
        var asset = new Assets();
        asset.setKosProfile(kosProfile);
        asset = assetsRepository.save(asset);
        var castleBuilding = new CastleBuilding();
        castleBuilding.setLevel(1L);
        castleBuilding.setKosProfile(kosProfile);
        castleBuilding = castleBuildingRepository.save(castleBuilding);
        var oldUserBase = new UserBase();
        oldUserBase.setCoordinate(new Coordinates(0L, 0L));
        oldUserBase.setActive(true);
        oldUserBase.setIslandName("Test island");
        oldUserBase.setKosProfile(kosProfile);
        kosProfile.setBase(oldUserBase);
        oldUserBase = userBaseRepository.save(oldUserBase);

        var newBase = new UserBase();
        newBase.setCoordinate(new Coordinates(1L, 1L));
        newBase.setActive(false);
        newBase.setKosProfile(null);
        newBase = userBaseRepository.save(newBase);

        // create battle
        var battle = new Battle();
        battle.setDefender(new BattleProfile().setKosProfile(kosProfile).setBattle(battle));
        battle.setStatus(BattleStatus.INIT);
        battle = battleRepository.save(battle);

        // add item wa11
        var item = itemRepository.findById(ItemId.WA_11).get();

        var userItem = new UserItem().setItem(item)
                                     .setIsUsed(false)
                                     .setAmount(1L)
                                     .setAsset(asset);
        userItem = userItemRepository.save(userItem);
        itemService.useItem(new UseItemCommand().setKosProfileId(kosProfile.getId()).setAmount(1L).setItemId(ItemId.WA_11));

        // check
        var kosProfileWithNewCoor = kosProfileRepository.findById(kosProfile.getId()).get();
        Assertions.assertNotEquals(oldUserBase.getCoordinates().getY(), kosProfileWithNewCoor.getBase().getCoordinates().getY());
        Assertions.assertNotEquals(oldUserBase.getCoordinates().getX(), kosProfileWithNewCoor.getBase().getCoordinates().getX());
        var elementCache = (UserBaseCache) seaElementService.findByXAndYFromCache(kosProfileWithNewCoor.getBase().getCoordinates().getX(),
                                                                                  kosProfileWithNewCoor.getBase().getCoordinates().getY()).get(0);
        Assertions.assertNotNull(elementCache);
        Assertions.assertEquals(kosProfileWithNewCoor.getId(), elementCache.getKosProfile().getId());
        Assertions.assertEquals(1, kosProfileWithNewCoor.getBase().getX());
        Assertions.assertEquals(1, kosProfileWithNewCoor.getBase().getY());
        Assertions.assertEquals(1, elementCache.getX());
        Assertions.assertEquals(1, elementCache.getY());
        battle = battleRepository.findById(battle.getId()).get();
        Assertions.assertEquals(1, battle.getCancelReason());
    }
}

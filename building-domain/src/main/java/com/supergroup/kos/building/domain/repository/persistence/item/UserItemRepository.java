package com.supergroup.kos.building.domain.repository.persistence.item;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.supergroup.kos.building.domain.constant.item.ItemId;
import com.supergroup.kos.building.domain.constant.item.ItemType;
import com.supergroup.kos.building.domain.constant.item.NameSpaceKey;
import com.supergroup.kos.building.domain.dto.item.DetailItemEffect;
import com.supergroup.kos.building.domain.dto.item.DetailUserItem;
import com.supergroup.kos.building.domain.model.item.Item;
import com.supergroup.kos.building.domain.model.item.UserItem;

public interface UserItemRepository extends JpaRepository<com.supergroup.kos.building.domain.model.item.UserItem, Long> {
    @Query("select i from Item i left join UserItem ui on ui.item.id = i.id where ui.asset.id = ?1")
    List<Item> getListItemByAssetId(Long assetId);

    @Query("select count(ui) from UserItem ui left join Item i on ui.item.id = i.id where ui.asset.id = ?1 and i.id = ?2 and ui.isUsed = false")
    Long amountItemByItemIdAndAssetId(Long assetId, ItemId itemId);

    @Query(value = "SELECT it.id as id, count(it.id) as amount, it.type as type, "
                   + " it.namespace as namespace, it.name as name, it.thumbnail as thumbnail, "
                   + " it.description as description ,it.expiry  as expiry , it.unit as unit "
                   + " from tbl_user_item  ui left join tbl_item  it "
                   + " on ui.item_id = it.id "
                   + " where ui.asset_id = ?1 "
                   + " and (?2 is null or it.type = ?2) "
                   + " and (?3 is null or it.namespace = ?3 ) "
                   + " and ui.is_used = false "
                   + " group by it.id; ", nativeQuery = true)
    List<DetailUserItem> getAllItemOfUser(Long assetId, String type, String namespace);

    @Query(value = "select tui.is_used as isUsed, tui.use_time as useTime, tui.expired_date as expiredDate from tbl_user_item tui left join tbl_item ti on tui.item_id = ti.id where ti.id = :itemId and tui.asset_id = :assetId order by tui.updated_at desc limit 1",
           nativeQuery = true)
    Optional<DetailItemEffect> getDetailItemEffect(@Param("assetId") Long assetId, @Param("itemId") String itemId);

    @Query(value = "select it.id as id, count(it.id) as amount, it.type as type, "
                   + " it.namespace as namespace, it.name as name, it.thumbnail as thumbnail, "
                   + " it.description as description, it.expiry as expiry, it.unit as unit "
                   + " from UserItem ui left join Item it "
                   + " on ui.item.id = it.id "
                   + " where ui.asset.id = :assetId "
                   + " and (:type is null or it.type = :type) "
                   + " and ((:namespaces) is null or it.namespace in (:namespaces)) "
                   + " and ui.isUsed = false "
                   + " group by it.id")
    List<DetailUserItem> getUserItemByInNamespace(@Param("assetId") Long assetId, @Param("type") ItemType type,
                                                  @Param("namespaces") List<NameSpaceKey> namespaces);

    Optional<UserItem> findFirstByAsset_IdAndItem_IdAndIsUsed(Long id, ItemId id1, Boolean isUsed);

    @Query("select ui from UserItem ui left join Item i on ui.item.id = i.id where ui.asset.id = ?1 and i.id = ?2 and ui.isUsed = true")
    List<UserItem> findUsedItemsByAssetIdAndItemId(Long assetId, ItemId itemId);

    @Query("select ui from UserItem ui left join Item i on ui.item.id = i.id where ui.asset.id = ?1 and i.id = ?2")
    List<UserItem> findUserItemsByAssetIdAndItemId(Long assetId, ItemId itemId);

    @Query("select ui from UserItem ui left join Assets a on ui.asset.id = a.id left join KosProfile k on k.id = a.kosProfile.id where k.id = ?1")
    List<UserItem> getAllUserItemByKosProfileId(Long kosProfileId);

    @Query("select ui from UserItem ui left join Assets a on ui.asset.id = a.id left join KosProfile k on k.id = a.kosProfile.id where k.id = ?1 and ui.isUsed = true")
    List<UserItem> getAllUsedItemByKosProfileId(Long kosProfileId);
}

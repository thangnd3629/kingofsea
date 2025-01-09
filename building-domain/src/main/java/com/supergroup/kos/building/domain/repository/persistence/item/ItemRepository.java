package com.supergroup.kos.building.domain.repository.persistence.item;

import java.util.Collection;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.supergroup.kos.building.domain.constant.item.ItemId;
import com.supergroup.kos.building.domain.constant.item.ItemType;
import com.supergroup.kos.building.domain.model.item.Item;

public interface ItemRepository extends JpaRepository<Item, ItemId> {
    @Query("Select it from Item it "
           + "WHERE (?1 is null OR it.type = ?1 ) ")
    Page<Item> getAllItem(ItemType type, Pageable pageable);

    @Query("select it from Item it")
    List<Item> getAll();

    List<Item> findByType(ItemType type);

    List<Item> findByIdIn(Collection<ItemId> ids);

    @Query("select count(i) > 0 from Item i where i.id = ?1 and i.status = com.supergroup.core.constant.BaseStatus.ACTIVATED")
    Boolean isActive(ItemId itemId);
}

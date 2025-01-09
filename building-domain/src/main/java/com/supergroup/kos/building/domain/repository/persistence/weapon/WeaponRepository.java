package com.supergroup.kos.building.domain.repository.persistence.weapon;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import com.supergroup.core.repository.BaseJpaRepository;
import com.supergroup.kos.building.domain.model.weapon.Weapon;

@Repository("WeaponRepositoryJpa")
public interface WeaponRepository extends BaseJpaRepository<Weapon> {

    @Query("select w from Weapon w where w.assets.kosProfile.id = ?1 and isDeleted is false")
    Page<Weapon> findByKosProfileId(@NonNull Long id, Pageable pageable);

    @Query("select w from Weapon w where w.assets.kosProfile.id = ?1 and isDeleted is false")
    List<Weapon> findByKosProfileId(@NonNull Long id);

    @Query("select w from Weapon w where w.assets.kosProfile.id = ?1 and w.motherShip is null and isDeleted is false")
    List<Weapon> findByKosProfileIdAndMotherShipNull(@NonNull Long id);

    @Query("select w from Weapon w where w.assets.kosProfile.id = ?1 and w.id = ?2 and isDeleted is false")
    Optional<Weapon> findByKosProfileIdAndWeaponId(@NonNull Long kosProfileId, @NonNull Long weaponId);

    @Query("select count(w) from Weapon w where w.weaponConfig.id = ?1 and w.assets.kosProfile.id = ?2 and isDeleted is false")
    Long countByWeaponConfigIdAndKosProfileId(@NonNull Long weaponConfigId, @NonNull Long kosProfileId);

    @Query("select w from Weapon w where w.weaponConfig.id = ?1 and w.assets.kosProfile.id = ?2 and isDeleted is false")
    List<Weapon> findByWeaponConfigIdAndKosProfileId(Long weaponConfigId, Long kosProfileId);

    @Query("update Weapon w set w.isDeleted=true where w.id = ?1")
    @Modifying
    void delete(Long weaponId);

    @Query("select count(w) > 0 from Weapon w where w.id = ?1 and w.status = com.supergroup.core.constant.BaseStatus.ACTIVATED")
    Boolean isActive(Long id);

}

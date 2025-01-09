package com.supergroup.kos.building.domain.repository.persistence.weapon;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import com.supergroup.core.repository.BaseJpaRepository;
import com.supergroup.kos.building.domain.model.weapon.WeaponSet;

@Repository("WeaponSetRepositoryJpa")
public interface WeaponSetRepository extends BaseJpaRepository<WeaponSet> {

    @Query("select w from WeaponSet w where w.assets.kosProfile.id = ?1")
    Page<WeaponSet> findByKosProfileId(@NonNull Long id, Pageable pageable);
    @Query("select w from WeaponSet w where w.assets.kosProfile.id = ?1")
    List<WeaponSet> findByKosProfileId(@NonNull Long id);
    @Query("select w from WeaponSet w where w.assets.kosProfile.id = ?1 and w.id = ?2")
    Optional<WeaponSet> findByKosProfileIdAndWeaponSetId(@NonNull Long kosProfileId, @NonNull Long weaponSetId);

    @Query("select count(w) from WeaponSet w where w.weaponSetConfig.id = ?1 and w.assets.kosProfile.id = ?2")
    Long countByWeaponSetConfigByIdAndKosProfileId(@NonNull Long weaponSetConfigId, @NonNull Long kosProfileId);

    @Query("select count(w) from WeaponSet w where w.assets.kosProfile.id = ?1")
    Long countByKosProfileId(Long id);



}

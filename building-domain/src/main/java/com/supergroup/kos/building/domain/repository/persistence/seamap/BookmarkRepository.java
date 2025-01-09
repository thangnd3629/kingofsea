package com.supergroup.kos.building.domain.repository.persistence.seamap;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.supergroup.kos.building.domain.model.seamap.Bookmark;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    Page<Bookmark> findBookmarkByKosProfileId(Long kosProfileId, Pageable pageable);


    Optional<Bookmark> findBookmarkByIdAndKosProfileId(Long id, Long kosProfileId);

    Boolean existsByCoordinatesXAndCoordinatesYAndKosProfileId(Long x, Long y, Long kosProfileId);

}

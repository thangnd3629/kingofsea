package com.supergroup.kos.building.domain.repository.persistence.seamap;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.supergroup.core.repository.BaseJpaRepository;
import com.supergroup.kos.building.domain.model.seamap.CoordinatesConflictNewBase;
import com.supergroup.kos.building.domain.model.seamap.ElementRefresh;
import com.supergroup.kos.building.domain.model.seamap.SeaElement;
import com.supergroup.kos.building.domain.model.seamap.UserBase;

@Repository("SeaElementRepositoryJpa")
public interface SeaElementRepository<T extends SeaElement> extends BaseJpaRepository<T> {

    @Query("select s from SeaElement s "
           + "where s.coordinates.x >= :xLeft "
           + "and s.coordinates.x <= :xRight "
           + "and s.coordinates.y >= :yBelow "
           + "and s.coordinates.y <= :yTop ")
    List<SeaElement> getElementsInArea(Double xLeft, Double xRight, Double yBelow, Double yTop);

    @Query("select se from SeaElement se where se.coordinates.x = ?1 and se.coordinates.y = ?2 and se.active = true")
    List<SeaElement> findByXAndY(Long x, Long y);

    Optional<UserBase> findFirstByActiveOrderByIdAsc(Boolean active);

//    @Query("select s from SeaElement s "
//           + "where s.isRefreshable = true")
//    List<SeaElement> getElementsRefresh();

    @Modifying
    @Query(value = "delete from tbl_element_sea se\n"
                   + "where se.id in (select se.id from tbl_element_sea se left join tbl_elements_config ec on se.elements_config_id = ec.id where ec.type in :types)",
           nativeQuery = true)
    void deleteByListType(List<String> types);

    @Query(value = "select * from tbl_element_sea se left join tbl_elements_config ec on se.elements_config_id = ec.id where ec.type in :types",
           nativeQuery = true)
    List<SeaElement> findByListType(List<String> types);

    @Query("select s from SeaElement s "
           + "where s.active = true")
    List<SeaElement> getElementsActive();

    @Query("SELECT s FROM SeaElement s WHERE s.active = true and s.id = ?1")
    Optional<SeaElement> findByIdAndIsActive(Long id);

    @Query(value = "select id, type, active from tbl_element_sea where "
                   + "(type IN ('RESOURCE', 'BOSS') and x >= (:x)  and x <= (:x + 1) and y >= (:y) and  y <= (:y + 1)) "
                   + "or  (type = 'USER' and x >= (:x - 1)  and x <= (:x + 1) and y >= (:y - 1) and  y <= (:y + 1)) "
            , nativeQuery = true)
    List<CoordinatesConflictNewBase> findCoordinatesConflictNewBase(Long x, Long y); // warring

    @Query(value = "select s.id as id, s.type as type, s.battle_id as battleId, s.mining_session_id as miningSessionId,"
           + "re.sea_activity_id as seaActivityId, s.x as x , s.y as y  "
           + "from tbl_element_sea as s left join tbl_mining_resource_session as re on s.mining_session_id = re.id "
           + "where s.is_refreshable = true", nativeQuery = true)
    List<ElementRefresh> getElementsRefresh();

    @Modifying
    @Query(value = "update tbl_element_sea set deleted = :deleted where id in :ids", nativeQuery = true)
    int updateDeletedElement(List<Long> ids, Boolean deleted);
}

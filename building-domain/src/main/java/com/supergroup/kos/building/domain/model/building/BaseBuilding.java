package com.supergroup.kos.building.domain.model.building;

import java.util.Objects;

import javax.persistence.EntityListeners;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToOne;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.supergroup.core.constant.ErrorCode;
import com.supergroup.core.model.BaseModel;
import com.supergroup.kos.building.domain.constant.BuildingName;
import com.supergroup.kos.building.domain.exception.TechRequirementException;
import com.supergroup.kos.building.domain.model.profile.KosProfile;
import com.supergroup.kos.building.domain.model.upgrade.UpgradeSession;
import com.supergroup.kos.building.domain.service.technology.TechnologyService;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author idev
 * All building entity must extend from this class
 */
@MappedSuperclass
@Getter
@Setter
@ToString
@NoArgsConstructor
@Accessors(chain = true)
@EntityListeners(AuditingEntityListener.class)
public class BaseBuilding extends BaseModel {

    private Long         level;
    private Boolean      isLock;
    private BuildingName name;
    private String       description;

    @ManyToOne
    @JoinColumn(name = "upgrade_session_id")
    private UpgradeSession upgradeSession;

    @OneToOne
    @JoinColumn(name = "kos_profile_id")
    private KosProfile kosProfile;

    /**
     * Check building is unlock,
     * If building is lock, throw exception
     */
    public void validUnlockBuilding(TechnologyService technologyService) {
        // check unlock building
        if (getIsLock()) {
            var techRequired = technologyService.findByUnlockBuildingName(name);
            throw new TechRequirementException(ErrorCode.BUILDING_IS_LOCKED, techRequired);
        }
    }

    public Boolean getIsLock() {
        return Objects.nonNull(isLock) ? isLock : true;
    }

}

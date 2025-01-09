package com.supergroup.kos.building.domain.model.item;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.supergroup.core.model.BaseModel;
import com.supergroup.kos.building.domain.model.asset.Assets;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Table(name = "tbl_user_item")
@Getter
@Setter
@Accessors(chain = true)
public class UserItem extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne
    @JoinColumn(name = "asset_id")
    private Assets asset;

    private LocalDateTime useTime;

    private Long duration;

    private LocalDateTime expiredDate;

    @Column(name = "is_used", columnDefinition = "boolean default false")
    private Boolean isUsed;

    @Transient
    private Long amount = 0L;

    @Transient
    private Boolean isExpired = false;
}

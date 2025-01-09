package com.supergroup.kos.building.domain.model.item;

import java.util.Collection;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.supergroup.core.constant.BaseStatus;
import com.supergroup.core.model.BaseModel;
import com.supergroup.kos.building.domain.constant.item.ItemId;
import com.supergroup.kos.building.domain.constant.item.ItemType;
import com.supergroup.kos.building.domain.constant.item.NameSpaceKey;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author idev
 * Item System
 */
@Getter
@Setter
@Entity
@Table(name = "tbl_item")
@Accessors(chain = true)
public class Item extends BaseModel {
    @Enumerated(EnumType.STRING)
    @Id
    private ItemId                 id;
    @Enumerated(EnumType.STRING)
    private NameSpaceKey           namespace;
    @Enumerated(EnumType.STRING)
    private ItemType               type;
    private String                 name;
    private String                 thumbnail;
    @OneToMany(mappedBy = "item", cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    private Collection<ItemEffect> effects;
    private String                 description;
    private Long                   expiry; // seconds
    private String                 unit;
    private String                 message;

    private BaseStatus status;
}

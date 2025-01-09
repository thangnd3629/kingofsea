package com.supergroup.kos.building.domain.model.item;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.supergroup.kos.building.domain.constant.item.EffectTarget;
import com.supergroup.kos.building.domain.constant.item.TypeParameter;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author idev
 * Effect of item
 */
@Getter
@Setter
@Entity
@Table(name = "tbl_item_effect")
@Accessors(chain = true)
public class ItemEffect {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long          id;
    private String        parameter;
    @Enumerated(EnumType.STRING)
    private TypeParameter typeParameter;
    @Enumerated(EnumType.STRING)
    private EffectTarget  target;
    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    private Item          item;
}

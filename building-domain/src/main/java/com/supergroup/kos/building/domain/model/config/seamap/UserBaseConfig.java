package com.supergroup.kos.building.domain.model.config.seamap;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@DiscriminatorValue("USER")
@Getter
@Setter
@Accessors(chain = true)
public class UserBaseConfig extends SeaElementConfig {
}

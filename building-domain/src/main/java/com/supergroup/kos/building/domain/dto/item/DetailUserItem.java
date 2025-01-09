package com.supergroup.kos.building.domain.dto.item;

public interface DetailUserItem {

    String getId();

    Long getAmount();

    String getNamespace();

    String getType();

    String getName();

    String getThumbnail();

    String getDescription();

    Long getExpiry(); // seconds

    String getUnit();
}

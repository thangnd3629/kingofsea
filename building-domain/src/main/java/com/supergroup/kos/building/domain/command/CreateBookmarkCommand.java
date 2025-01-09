package com.supergroup.kos.building.domain.command;

import com.supergroup.kos.building.domain.constant.seamap.LabelBookmark;

import lombok.Data;

@Data
public class CreateBookmarkCommand {
    private Long          x;
    private Long          y;
    private String        description;
    private LabelBookmark label;
    private Long          kosProfileId;

}

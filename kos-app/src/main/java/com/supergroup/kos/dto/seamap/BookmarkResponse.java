package com.supergroup.kos.dto.seamap;

import javax.validation.constraints.NotNull;

import com.supergroup.kos.building.domain.constant.seamap.LabelBookmark;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(chain = true)
public class BookmarkResponse {
    private Long          id;
    private LabelBookmark label;
    private String        description;
    @NotNull
    private Double        x;

    @NotNull
    private Double        y;
}

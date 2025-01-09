package com.supergroup.kos.dto.seamap;

import javax.validation.constraints.NotNull;

import com.supergroup.kos.building.domain.constant.seamap.LabelBookmark;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateBookmarkRequest {
    @NotNull
    private Double        x;
    @NotNull
    private Double        y;
    private String        description;
    @NotNull
    private LabelBookmark label;

}

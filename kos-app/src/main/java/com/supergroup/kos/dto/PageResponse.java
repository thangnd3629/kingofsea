package com.supergroup.kos.dto;

import java.util.Collection;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Getter
@Setter
public class PageResponse<T> {
    private Long          total;
    private Collection<T> data;
}
package com.supergroup.kos.dto.technology;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class TechnologyTreeResponse{
	private List<TechnologyTreeItem> tree;
}
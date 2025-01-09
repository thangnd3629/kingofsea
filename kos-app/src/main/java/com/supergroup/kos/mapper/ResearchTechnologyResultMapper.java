package com.supergroup.kos.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.supergroup.kos.building.domain.model.research.ResearchResult;
import com.supergroup.kos.dto.technology.ResearchTechnologyResponse;

@Mapper
public interface ResearchTechnologyResultMapper {

    List<ResearchTechnologyResponse> toResponse(List<ResearchResult> researchResult);

    ResearchTechnologyResponse toResponse(ResearchResult researchResult);
}

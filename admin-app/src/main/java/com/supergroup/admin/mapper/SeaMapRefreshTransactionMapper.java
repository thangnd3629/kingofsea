package com.supergroup.admin.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.supergroup.admin.dto.SeaMapRefreshTransactionResponse;
import com.supergroup.kos.building.domain.model.seamap.SeaMapRefreshTransaction;

@Mapper
public interface SeaMapRefreshTransactionMapper {
    SeaMapRefreshTransactionResponse toResponse(SeaMapRefreshTransaction seaMapRefreshTransaction);
    List<SeaMapRefreshTransactionResponse> toResponses(List<SeaMapRefreshTransaction> seaMapRefreshTransactions);
}

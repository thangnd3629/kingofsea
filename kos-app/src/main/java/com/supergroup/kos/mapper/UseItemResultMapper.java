package com.supergroup.kos.mapper;

import org.mapstruct.Mapper;

import com.supergroup.kos.building.domain.model.item.UseItemResult;
import com.supergroup.kos.building.domain.model.item.UseMoveBaseItemResult;
import com.supergroup.kos.dto.item.UseItemResponse;
import com.supergroup.kos.dto.item.UseMoveBaseItemResponse;

@Mapper
public interface UseItemResultMapper {
    default UseItemResponse map(UseItemResult useItemResult) {
        if (useItemResult instanceof UseMoveBaseItemResult) {
            return map((UseMoveBaseItemResult) useItemResult);
        }
        return null;
    }

    UseMoveBaseItemResponse map(UseMoveBaseItemResult useMoveBaseItemResult);
}

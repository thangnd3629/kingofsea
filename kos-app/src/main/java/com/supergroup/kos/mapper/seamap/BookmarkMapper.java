package com.supergroup.kos.mapper.seamap;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import com.supergroup.kos.building.domain.command.CreateBookmarkCommand;
import com.supergroup.kos.building.domain.model.seamap.Bookmark;
import com.supergroup.kos.dto.seamap.BookmarkResponse;
import com.supergroup.kos.dto.seamap.CreateBookmarkRequest;

@Mapper
public interface BookmarkMapper {
    @Mappings(
            {
                    @Mapping(source = "coordinates.x", target = "x"),
                    @Mapping(source = "coordinates.y", target = "y")
            }
    )
    BookmarkResponse toDTO(Bookmark bookmark);
    CreateBookmarkCommand toCommand(CreateBookmarkRequest request);
}

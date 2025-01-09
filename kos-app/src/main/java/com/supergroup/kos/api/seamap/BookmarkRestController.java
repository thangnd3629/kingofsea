package com.supergroup.kos.api.seamap;

import java.util.stream.Collectors;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.supergroup.kos.building.domain.command.CreateBookmarkCommand;
import com.supergroup.kos.building.domain.command.DeleteBookmarkCommand;
import com.supergroup.kos.building.domain.command.GetPageBookmarkCommand;
import com.supergroup.kos.building.domain.command.UserCommand;
import com.supergroup.kos.building.domain.model.profile.KosProfile;
import com.supergroup.kos.building.domain.model.seamap.Bookmark;
import com.supergroup.kos.building.domain.service.profile.KosProfileService;
import com.supergroup.kos.building.domain.service.seamap.BookmarkService;
import com.supergroup.kos.dto.PageResponse;
import com.supergroup.kos.dto.seamap.BookmarkResponse;
import com.supergroup.kos.dto.seamap.CreateBookmarkRequest;
import com.supergroup.kos.mapper.seamap.BookmarkMapper;
import com.supergroup.kos.util.AuthUtil;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/seamap/bookmarks")
@RequiredArgsConstructor
public class BookmarkRestController {
    public final BookmarkService   bookmarkService;
    public final KosProfileService kosProfileService;
    public final BookmarkMapper    bookmarkMapper;

    @GetMapping("")
    public ResponseEntity<PageResponse<BookmarkResponse>> getBookmark(Pageable pageable) {
        KosProfile kosProfile = kosProfileService.getKosProfile(new UserCommand().setUserId(AuthUtil.getUserId()));
        Page<Bookmark> bookmarks = bookmarkService.getPageBookmarks(
                new GetPageBookmarkCommand().setKosProfileId(kosProfile.getId()).setPageable(pageable)

                                                                   );
        PageResponse<BookmarkResponse> bookmarkPageResponse = new PageResponse<>();
        bookmarkPageResponse.setData(bookmarks.toList().stream().map(bookmarkMapper::toDTO).collect(Collectors.toList()));
        bookmarkPageResponse.setTotal(bookmarks.getTotalElements());
        return ResponseEntity.ok(bookmarkPageResponse);
    }

    @PostMapping("")
    public ResponseEntity<BookmarkResponse> addBookmark(@RequestBody @Valid CreateBookmarkRequest request) {
        KosProfile kosProfile = kosProfileService.getKosProfile(new UserCommand().setUserId(AuthUtil.getUserId()));
        CreateBookmarkCommand command = bookmarkMapper.toCommand(request);
        command.setKosProfileId(kosProfile.getId());
        Bookmark bookmark = bookmarkService.addBookmark(command);
        return ResponseEntity.ok(bookmarkMapper.toDTO(bookmark));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<BookmarkResponse> deleteBookmark(@PathVariable(name = "id") @NotNull @Valid Long bookmarkId) {
        KosProfile kosProfile = kosProfileService.getKosProfile(new UserCommand().setUserId(AuthUtil.getUserId()));
        Long id = bookmarkService.deleteBookmark(new DeleteBookmarkCommand().setBookmarkId(bookmarkId).setKosProfileId(kosProfile.getId()));
        return ResponseEntity.ok(new BookmarkResponse().setId(id));

    }
}

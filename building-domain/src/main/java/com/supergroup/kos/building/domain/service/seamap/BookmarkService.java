package com.supergroup.kos.building.domain.service.seamap;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.supergroup.core.constant.ErrorCode;
import com.supergroup.core.exception.KOSException;
import com.supergroup.kos.building.domain.command.CreateBookmarkCommand;
import com.supergroup.kos.building.domain.command.DeleteBookmarkCommand;
import com.supergroup.kos.building.domain.command.GetPageBookmarkCommand;
import com.supergroup.kos.building.domain.model.profile.KosProfile;
import com.supergroup.kos.building.domain.model.seamap.Bookmark;
import com.supergroup.kos.building.domain.model.seamap.Coordinates;
import com.supergroup.kos.building.domain.repository.persistence.seamap.BookmarkRepository;
import com.supergroup.kos.building.domain.service.profile.KosProfileService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookmarkService {
    private final BookmarkRepository bookmarkRepository;
    private final KosProfileService  kosProfileService;

    public Bookmark addBookmark(CreateBookmarkCommand createBookmarkCommand) {
        KosProfile kosProfile = kosProfileService.getKosProfileById(createBookmarkCommand.getKosProfileId());
        if (bookmarkRepository.existsByCoordinatesXAndCoordinatesYAndKosProfileId(createBookmarkCommand.getX(), createBookmarkCommand.getY(), kosProfile.getId())){
            throw KOSException.of(ErrorCode.BOOK_MARK_EXISTED);
        }
        Bookmark bookmark = new Bookmark()
                .setLabel(createBookmarkCommand.getLabel())
                .setCoordinates(new Coordinates().setX(createBookmarkCommand.getX()).setY(createBookmarkCommand.getY()))
                .setKosProfile(kosProfile)
                .setDescription(createBookmarkCommand.getDescription());
        bookmarkRepository.save(bookmark);
        return bookmark;
    }

    public Long deleteBookmark(DeleteBookmarkCommand deleteBookmarkCommand) {
        Long bookmarkId = deleteBookmarkCommand.getBookmarkId();
        Long kosProfileId = deleteBookmarkCommand.getKosProfileId();
        Optional<Bookmark> nullable = bookmarkRepository.findBookmarkByIdAndKosProfileId(bookmarkId, kosProfileId);
        if (nullable.isEmpty()) {
            throw new KOSException(ErrorCode.BOOKMARK_NOT_FOUND);
        }
        Bookmark bookmark = nullable.get();
        bookmarkRepository.delete(bookmark);
        return bookmarkId;
    }

    public Page<Bookmark> getPageBookmarks(
            GetPageBookmarkCommand command
                                          ) {
        return bookmarkRepository.findBookmarkByKosProfileId(command.getKosProfileId(), command.getPageable());

    }
}


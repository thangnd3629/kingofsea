package com.supergroup.kos.middleware;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.supergroup.kos.building.domain.command.UserCommand;
import com.supergroup.kos.building.domain.model.item.UserItem;
import com.supergroup.kos.building.domain.repository.persistence.item.UserItemRepository;
import com.supergroup.kos.building.domain.service.profile.KosProfileService;
import com.supergroup.kos.building.domain.service.seamap.item.ItemService;
import com.supergroup.kos.config.AccessSession;
import com.supergroup.kos.util.AuthUtil;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ItemMiddleware extends OncePerRequestFilter {

    private final ItemService        itemService;
    private final KosProfileService  kosProfileService;
    private final UserItemRepository userItemRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof AccessSession) {
                var kosProfile = kosProfileService.getKosProfile(new UserCommand().setUserId(AuthUtil.getUserId()));
                // must get user item like this because lazy hibernate session error
                var userItems = userItemRepository.getAllUsedItemByKosProfileId(kosProfile.getId());
                for (UserItem userItem : userItems) {
                    if (itemService.isExpired(userItem)) {
                        itemService.deactivate(userItem);
                    }
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            // ignore
        }
        filterChain.doFilter(request, response);
    }
}

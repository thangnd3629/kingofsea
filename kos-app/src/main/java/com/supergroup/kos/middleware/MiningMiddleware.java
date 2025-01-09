package com.supergroup.kos.middleware;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.data.util.Pair;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import com.supergroup.kos.building.domain.command.UserCommand;
import com.supergroup.kos.building.domain.service.building.CastleBuildingService;
import com.supergroup.kos.building.domain.service.building.StoneMineService;
import com.supergroup.kos.building.domain.service.building.WoodMineService;
import com.supergroup.kos.building.domain.service.profile.KosProfileService;
import com.supergroup.kos.config.AccessSession;
import com.supergroup.kos.util.AuthUtil;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MiningMiddleware extends OncePerRequestFilter {

    private static final List<Pair<HttpMethod, String>> STONE_API = Arrays.asList(
            Pair.of(HttpMethod.GET, "/v1/building/stone"),
            Pair.of(HttpMethod.GET, "/v1/user/kos/asset/assets"),
            Pair.of(HttpMethod.GET, "/v1/mining/data"),
            Pair.of(HttpMethod.POST, "/v1/building/**/upgrade"),
            Pair.of(HttpMethod.POST, "/v1/weapon-set"),
            Pair.of(HttpMethod.PUT, "/v1/weapon-set/**/upgrade"),
            Pair.of(HttpMethod.GET, "/v1/building/storage/**"),
            Pair.of(HttpMethod.PUT, "/v1/escort-ship-group/upgrade"),
            Pair.of(HttpMethod.POST, "/v1/escort-ship/build"),
            Pair.of(HttpMethod.PUT, "/v1/building/stone/worker"));

    private static final List<Pair<HttpMethod, String>> WOOD_API = Arrays.asList(
            Pair.of(HttpMethod.GET, "/v1/building/wood"),
            Pair.of(HttpMethod.GET, "/v1/user/kos/asset/assets"),
            Pair.of(HttpMethod.GET, "/v1/mining/data"),
            Pair.of(HttpMethod.POST, "/v1/building/**/upgrade"),
            Pair.of(HttpMethod.POST, "/v1/weapon-set"),
            Pair.of(HttpMethod.PUT, "/v1/weapon-set/**/upgrade"),
            Pair.of(HttpMethod.GET, "/v1/building/storage/**"),
            Pair.of(HttpMethod.PUT, "/v1/escort-ship-group/upgrade"),
            Pair.of(HttpMethod.POST, "/v1/escort-ship/build"),
            Pair.of(HttpMethod.PUT, "/v1/building/wood/worker"));

    private static final List<Pair<HttpMethod, String>> PEOPLE_GOLD_API = Arrays.asList(
            Pair.of(HttpMethod.GET, "/v1/user/kos/asset/assets"),
            Pair.of(HttpMethod.GET, "/v1/mining/data"),
            Pair.of(HttpMethod.PUT, "/v1/building/**/worker"),
            Pair.of(HttpMethod.POST, "/v1/building/**/upgrade"),
            Pair.of(HttpMethod.GET, "/v1/user/castle/overview"),
            Pair.of(HttpMethod.POST, "/v1/weapon-set"),
            Pair.of(HttpMethod.GET, "/v1/building/storage/**"),
            Pair.of(HttpMethod.PUT, "/v1/weapon-set/**/upgrade"),
            Pair.of(HttpMethod.POST, "/v1/escort-ship/build"),
            Pair.of(HttpMethod.PUT, "/v1/escort-ship-group/upgrade"),
            Pair.of(HttpMethod.PUT, "/v1/building/**/worker"));

    private final StoneMineService      stoneMineService;
    private final WoodMineService       woodMineService;
    private final KosProfileService     kosProfileService;
    private final CastleBuildingService castleBuildingService;

    private AntPathMatcher antPathMatcher;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        antPathMatcher = new AntPathMatcher();
        try {
            if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof AccessSession) {
                var kosProfile = kosProfileService.getKosProfile(new UserCommand().setUserId(AuthUtil.getUserId()));
                if (isMatch(STONE_API, request)) {
                    stoneMineService.claimStone(kosProfile.getId());
                }
                if (isMatch(WOOD_API, request)) {
                    woodMineService.claimWood(kosProfile.getId());
                }
                if (isMatch(PEOPLE_GOLD_API, request)) {
                    castleBuildingService.claimPeopleAndGold(kosProfile.getId());
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        filterChain.doFilter(request, response);
    }

    private boolean isMatch(List<Pair<HttpMethod, String>> listApi, HttpServletRequest request) {
        for (Pair<HttpMethod, String> item : listApi) {
            if (item.getFirst().matches(request.getMethod())
                && antPathMatcher.match(item.getSecond(), request.getRequestURI())) {
                return true;
            }
        }
        return false;
    }
}

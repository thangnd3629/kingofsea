package com.supergroup.kos.api.seamap;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.supergroup.kos.building.domain.command.GetElementsByAreaCommand;
import com.supergroup.kos.building.domain.command.UserCommand;
import com.supergroup.kos.building.domain.model.profile.KosProfile;
import com.supergroup.kos.building.domain.model.seamap.Coordinates;
import com.supergroup.kos.building.domain.service.profile.KosProfileService;
import com.supergroup.kos.building.domain.service.seamap.MapService;
import com.supergroup.kos.mapper.elements.ElementMapper;
import com.supergroup.kos.util.AuthUtil;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/seamap/area")
@RequiredArgsConstructor
public class SeaMapAreaRestController {

    private final MapService        mapService;
    private final ElementMapper     elementMapper;
    private final KosProfileService kosProfileService;

    @GetMapping
    public ResponseEntity<?> getMap(@RequestParam(name = "x") Double x,
                                    @RequestParam(name = "y") Double y,
                                    @RequestParam(name = "width") Double width,
                                    @RequestParam(name = "height") Double height) {
        KosProfile kosProfile = kosProfileService.getKosProfile(new UserCommand().setUserId(AuthUtil.getUserId()));
        var elements = mapService.getElementsByArea(new GetElementsByAreaCommand(new Coordinates(x.longValue(), y.longValue()),
                                                                                 width.longValue(),
                                                                                 height.longValue(),
                                                                                 kosProfile));
        return ResponseEntity.ok(elementMapper.maps(elements));
    }

}

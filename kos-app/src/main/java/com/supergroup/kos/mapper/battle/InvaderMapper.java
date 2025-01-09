package com.supergroup.kos.mapper.battle;

import java.util.Objects;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.supergroup.asset.service.AssetService;
import com.supergroup.kos.building.domain.command.KosProfileCommand;
import com.supergroup.kos.building.domain.model.seamap.Invader;
import com.supergroup.kos.building.domain.model.seamap.SeaActivity;
import com.supergroup.kos.building.domain.model.seamap.ShipLineUp;
import com.supergroup.kos.building.domain.service.building.CastleBuildingService;
import com.supergroup.kos.dto.battle.InvaderResponse;
import com.supergroup.kos.dto.seamap.activity.ShipLineUpDTO;
import com.supergroup.kos.mapper.seamap.activity.LineUpMapper;

@Mapper
public abstract class InvaderMapper {

    @Autowired
    protected AssetService          assetService;
    @Autowired
    protected LineUpMapper          lineUpMapper;
    @Autowired
    protected CastleBuildingService castleBuildingService;
    @Autowired
    protected BattleReportMapper    battleReportMapper;

    @Transactional
    public InvaderResponse map(Invader invader) {
        var invaderResponse = new InvaderResponse();
        var kosProfile = invader.getKosProfileInvader();

        invaderResponse.setName(kosProfile.getBase().getIslandName());
        invaderResponse.setLevel(castleBuildingService.getCastleBuilding(new KosProfileCommand().setKosProfileId(kosProfile.getId())).getLevel());
        invaderResponse.setAvatarUrl(assetService.getUrl(kosProfile.getUser().getUserProfile().getAvatarUrl()));
        invaderResponse.setCoordinates(battleReportMapper.map(kosProfile.getBase().getCoordinates()));
        invaderResponse.setShipLineUps(invader.getActivitiesOnOccupiedBase().stream()
                                              .map(this::toShipLineUpDTO)
                                              .collect(Collectors.toList()));
        return invaderResponse;
    }

    public ShipLineUpDTO toShipLineUpDTO(SeaActivity activity) {
        ShipLineUpDTO shipLineUpDTO = new ShipLineUpDTO();
        ShipLineUp lineUp = activity.getLineUp();
        if (Objects.nonNull(lineUp)) {
            shipLineUpDTO = lineUpMapper.toDto(lineUp);
        }
        if (Objects.nonNull(activity.getScout())) {
            shipLineUpDTO.setNumberArmy(activity.getScout().getSoliderRemain());
        }
        return shipLineUpDTO;
    }
}

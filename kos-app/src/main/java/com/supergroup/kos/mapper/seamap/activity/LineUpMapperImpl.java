package com.supergroup.kos.mapper.seamap.activity;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.supergroup.asset.service.AssetService;
import com.supergroup.kos.building.domain.model.seamap.EscortShipSquad;
import com.supergroup.kos.building.domain.model.seamap.ShipLineUp;
import com.supergroup.kos.building.domain.model.ship.MotherShip;
import com.supergroup.kos.building.domain.repository.persistence.ship.EscortShipLevelConfigDataSource;
import com.supergroup.kos.dto.seamap.activity.EscortShipSquadDTO;
import com.supergroup.kos.dto.seamap.activity.ShipLineUpDTO;
import com.supergroup.kos.mapper.EscortShipMapper;
import com.supergroup.kos.mapper.MotherShipMapper;
import com.supergroup.kos.mapper.WeaponMapper;
import com.supergroup.kos.mapper.WeaponSetMapper;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class LineUpMapperImpl implements LineUpMapper {
    private final WeaponMapper                    weaponMapper;
    private final WeaponSetMapper                 weaponSetMapper;
    private final AssetService                    assetService;
    private final MotherShipMapper                motherShipMapper;
    private final EscortShipMapper                escortShipMapper;
    private final EscortShipLevelConfigDataSource escortShipLevelConfigDataSource;

    @Override
    public ShipLineUpDTO toDto(ShipLineUp model) {
        if (Objects.isNull(model)) {return null;}
        ShipLineUpDTO lineUpDTO = new ShipLineUpDTO();
        lineUpDTO.setId(model.getId());
        lineUpDTO.setUpdatedAt(model.getUpdatedAt());
        //mother ship mapper
        MotherShip motherShip = model.getMotherShip();
        var thumbnail = assetService.getUrl(motherShip.getMotherShipConfigQualityConfig().getMotherShipConfig().getThumbnail());
        lineUpDTO.setMotherShipThumbnail(thumbnail);
        var motherShipResponse = motherShipMapper.toDTO(motherShip);
        motherShipResponse.getModel().setThumbnail(thumbnail);
        var weaponResponses = motherShip.getWeapons().stream().map(weapon -> {
            var thumbnailWeapon = assetService.getUrl(weapon.getWeaponConfig().getThumbnail());
            var weaponResponse = weaponMapper.toDTO(weapon);
            weaponResponse.getModel().setThumbnail(thumbnailWeapon);
            return weaponResponse;
        }).collect(Collectors.toList());
        var weaponSetResponses = motherShip.getWeaponSets().stream().map(weaponSet -> {
            var thumbnailWeaponSet = assetService.getUrl(weaponSet.getWeaponSetConfig().getThumbnail());
            var weaponResponse = weaponSetMapper.toDTO(weaponSet);
            weaponResponse.getModel().setThumbnail(thumbnailWeaponSet);
            return weaponResponse;
        }).collect(Collectors.toList());
        motherShipResponse.setWeapons(weaponResponses);
        motherShipResponse.setWeaponSets(weaponSetResponses);
        lineUpDTO.setMotherShip(motherShipResponse);

        List<EscortShipSquad> escortShipSquads = model.getEscortShipSquad();

        //escort ship mapper

        List<EscortShipSquadDTO> escortShipSquadDtos = escortShipSquads.stream().map(escortShipSquad -> {
                                                                                         var escortShip = escortShipSquad.getEscortShip();
                                                                                         var escortShipLevelConfig = escortShipLevelConfigDataSource.getByTypeAndLevel(escortShip.getEscortShipConfig().getType(),
                                                                                                                                                                       escortShip.getLevel());
                                                                                         var response = escortShipMapper.toDTO(escortShip);
                                                                                         response.getModel().setThumbnail(thumbnail);
                                                                                         response.setPercentStat(escortShipLevelConfig.getPercentStat());
                                                                                         var escortShipSquadResponse = new EscortShipSquadDTO();
                                                                                         escortShipSquadResponse.setAmount(escortShipSquad.getAmount() - escortShipSquad.getKilled());
                                                                                         escortShipSquadResponse.setEscortShip(response);
                                                                                         return escortShipSquadResponse;
                                                                                     }
                                                                                    ).collect(Collectors.toList());
        lineUpDTO.setEscortShips(escortShipSquadDtos);
        return lineUpDTO;

    }

    @Override
    public List<ShipLineUpDTO> toDtos(Collection<ShipLineUp> models) {
        return models.stream().map(this::toDto).collect(Collectors.toList());
    }
}

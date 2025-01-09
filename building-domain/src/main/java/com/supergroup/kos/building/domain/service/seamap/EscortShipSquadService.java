package com.supergroup.kos.building.domain.service.seamap;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.supergroup.kos.building.domain.model.seamap.EscortShipSquad;
import com.supergroup.kos.building.domain.repository.persistence.seamap.EscortShipSquadRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EscortShipSquadService {
    private final EscortShipSquadRepository escortShipSquadRepository;


    public EscortShipSquad save(EscortShipSquad escortShipSquad) {
        return escortShipSquadRepository.save(escortShipSquad);
    }

    public List<EscortShipSquad> saveAll(List<EscortShipSquad> escortShipSquadList) {
        return escortShipSquadRepository.saveAll(escortShipSquadList);
    }

    public List<EscortShipSquad> finByIdIn(List<Long> ids) {
        return escortShipSquadRepository.findByIdIn(ids);
    }

    public Optional<EscortShipSquad> findByEscortShipIdAndLineUp(Long escortShipId, Long lineUpId) {
        return escortShipSquadRepository.findByEscortShipIdAndLineUpId(escortShipId, lineUpId);
    }


}

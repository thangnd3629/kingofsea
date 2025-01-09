package com.supergroup.kos.building.domain.service.seamap;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.supergroup.kos.building.domain.command.SaveOrUpdateElementCommand;
import com.supergroup.kos.building.domain.constant.seamap.BossSeaStatus;
import com.supergroup.kos.building.domain.dto.battle.RevivalBossEvent;
import com.supergroup.kos.building.domain.model.config.seamap.BossSeaConfig;
import com.supergroup.kos.building.domain.model.seamap.BossSea;
import com.supergroup.kos.building.domain.model.seamap.SeaElement;
import com.supergroup.kos.building.domain.repository.persistence.seamap.SeaElementConfigRepository;
import com.supergroup.kos.building.domain.repository.persistence.seamap.SeaElementRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BossSeaService {
    private final MapService                       mapService;
    private final SeaElementRepository<SeaElement> elementPersistenceRepository;
    private final SeaElementConfigRepository       seaElementConfigRepository;
    private final ApplicationEventPublisher        publisher;

    public BossSea findById(Long id) {
        Optional<SeaElement> nullable = elementPersistenceRepository.findById(id);
        if (nullable.isPresent() && nullable.get() instanceof BossSea) {
            return (BossSea) nullable.get();
        }
        return null;
    }

    @Transactional
    public void updateHp(Long bossSeaId, Long currentHp) {
        BossSea bossSea = findById(bossSeaId);
        if (Objects.nonNull(bossSea)) {
            Optional optionalSeaElement = seaElementConfigRepository.findBossSeaConfigById(bossSea.getSeaElementConfig().getId());
            if (optionalSeaElement.isPresent() && optionalSeaElement.get() instanceof BossSeaConfig) {
                BossSeaConfig bossSeaConfig = (BossSeaConfig) optionalSeaElement.get();
                Long maxHp = bossSeaConfig.getBossHp();
                if (currentHp <= 0) {
                    bossSea.setHpLost(maxHp)
                           .setStatus(BossSeaStatus.REVIVING)
                           .setTimeRevivingEnd(LocalDateTime.now().plusSeconds(bossSeaConfig.getBossTimeRespawn()));
                    // send message to revive boss
                    publisher.publishEvent(new RevivalBossEvent(bossSea));
                } else {
                    bossSea.setHpLost(maxHp - currentHp)
                           .setStatus(BossSeaStatus.NORMAL);
                }
                mapService.saveOrUpdateElement(new SaveOrUpdateElementCommand(bossSea));
            }
        }
    }
}

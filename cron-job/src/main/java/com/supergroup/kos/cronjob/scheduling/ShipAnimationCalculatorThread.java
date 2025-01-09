package com.supergroup.kos.cronjob.scheduling;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.supergroup.core.exception.KOSException;
import com.supergroup.kos.building.domain.command.SaveOrUpdateElementCommand;
import com.supergroup.kos.building.domain.model.seamap.Coordinates;
import com.supergroup.kos.building.domain.model.seamap.ShipElement;
import com.supergroup.kos.building.domain.repository.persistence.seamap.ShipElementRepository;
import com.supergroup.kos.building.domain.service.seamap.MapService;
import com.supergroup.kos.building.domain.utils.DistanceUtils;
import com.supergroup.kos.building.domain.utils.SeaMapCoordinatesUtils;

import io.sentry.Sentry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Configuration
@EnableScheduling
@Slf4j
public class ShipAnimationCalculatorThread {
    private final MapService mapService;
    private final ShipElementRepository shipElementRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Scheduled(fixedRate = 5000)
    public void run() {
        try {
            List<ShipElement> movingObjects = shipElementRepository.findActiveShipElement();
            for (ShipElement shipElement : movingObjects) {
                if (Objects.isNull(shipElement)) {
                    continue;
                }
                long elapsedTime = ChronoUnit.SECONDS.between(shipElement.getStartTime(),
                                                              LocalDateTime.now());
                long expectedTravellingDuration = (long) (DistanceUtils.getDistance(shipElement.getStart(), shipElement.getEnd())
                                                          / shipElement.getSpeed());
                if (elapsedTime > expectedTravellingDuration) {
                    shipElement.setActive(false); //todo : monitor if ship element still active when activity ended
                }
                Coordinates currentPosition = SeaMapCoordinatesUtils.getCurrentLocation(shipElement.getStart(), shipElement.getEnd(),
                                                                                        shipElement.getSpeed(),
                                                                                        Math.min(elapsedTime, expectedTravellingDuration));
                shipElement.setCoordinate(currentPosition);
                try {
                    mapService.saveOrUpdateElement(new SaveOrUpdateElementCommand(shipElement));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            if (e instanceof KOSException) {
                e.printStackTrace();
            } else {
                Sentry.captureException(e);
                throw e;
            }
        }
    }
}

package com.supergroup.kos.cronjob.scheduling;

import java.sql.Time;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.supergroup.core.exception.KOSException;
import com.supergroup.kos.building.domain.mapper.elements.transaction.SeaMapTransactionMapper;
import com.supergroup.kos.building.domain.model.seamap.RefreshNpcAndMineResult;
import com.supergroup.kos.building.domain.model.seamap.SeaElement;
import com.supergroup.kos.building.domain.model.seamap.SeaMapRefreshTransaction;
import com.supergroup.kos.building.domain.service.config.KosConfigService;
import com.supergroup.kos.building.domain.service.seamap.ElementsConfigService;
import com.supergroup.kos.building.domain.service.seamap.RefreshNpcAndMineService;
import com.supergroup.kos.building.domain.service.seamap.SeaElementService;
import com.supergroup.kos.building.domain.service.seamap.SeaMapRefreshTransactionService;
import com.supergroup.kos.building.domain.service.seamap.ValidUserBaseService;

import io.sentry.Sentry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class SchedulingJobRefreshNpcAndMine {
    private final KosConfigService                kosConfigService;
    private final RefreshNpcAndMineService        refreshNpcAndMineService;
    private final SeaMapRefreshTransactionService seaMapRefreshTransactionService;
    private final ValidUserBaseService            validUserBaseService;
    private final SeaElementService               seaElementService;
    private final SeaMapTransactionMapper         seaMapTransactionMapper;
    private final ElementsConfigService           elementsConfigService;

    @Scheduled(fixedDelay = 60, timeUnit = TimeUnit.SECONDS)
    public void refreshNpcAndMine() throws InterruptedException, JsonProcessingException {
        try {
            log.info("Refresh NPC and Mine");
            if (canRefresh()) {
                log.info("Start refresh npc and mine");
                LocalDateTime timeStart = LocalDateTime.now();
                RefreshNpcAndMineResult result = refreshNpcAndMineService.refreshNpcAndMine();
                log.info("Refresh done!");

                // create SeaMapRefreshTransaction
                SeaMapRefreshTransaction seaMapRefreshTransaction = createSeaMapRefreshTransaction(result);
                Thread.sleep(10000);
                updateSeaMapRefreshTransaction(seaMapRefreshTransaction);
                Long duration = Duration.between(timeStart, LocalDateTime.now()).getSeconds();
                seaMapRefreshTransactionService.save(seaMapRefreshTransaction.setDuration(duration));
                log.info("Create SeaMapRefreshTransaction done! : time refresh {}", duration);
            } else {
                log.info("Can't refresh!");
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

    @Scheduled(fixedDelay = 5, timeUnit = TimeUnit.MINUTES)
    public void validUserBaseJob() throws InterruptedException {
        log.info("Valid user base");
        LocalDateTime timeStart = LocalDateTime.now();
        validUserBaseService.validBaseUser();
        log.info("Valid user done! , time : {}", Duration.between(timeStart, LocalDateTime.now()).getSeconds());
    }

    private SeaMapRefreshTransaction createSeaMapRefreshTransaction(RefreshNpcAndMineResult result) {
        var now = LocalDateTime.now();
        SeaMapRefreshTransaction seaMapRefreshTransaction = new SeaMapRefreshTransaction();
        seaMapRefreshTransaction.setTimeRefresh(now)
                                .setTotalElementDeleted(result.getTotalElementDeleted())
                                .setTotalElementNotDeleted(result.getTotalElementNotDeleted())
                                .setTotalElementAccordingBaseCreated(result.getTotalElementAccordingBaseCreated())
                                .setTotalElementAccordingZoneSeaCreated(result.getTotalElementAccordingZoneSeaCreated());
        return seaMapRefreshTransaction;
    }

    private Boolean canRefresh() {

        List<Time> listTimeRefresh = kosConfigService.getSeaMapRefreshConfig().getListTimeRefresh();
        Optional<SeaMapRefreshTransaction> seaMapRefreshTransaction = seaMapRefreshTransactionService.findLastRefreshTransaction();
        LocalDateTime now = LocalDateTime.now();

        // if transaction is empty, can refresh
        if (seaMapRefreshTransaction.isEmpty()) {
            return true;
        }

        // otherwise, check time
        LocalDateTime lastTimeRefreshDateTime = seaMapRefreshTransaction.get().getTimeRefresh();
        Time lastRefreshTime = Time.valueOf(lastTimeRefreshDateTime.toLocalTime());
        Time timeNow = Time.valueOf(now.toLocalTime());
        boolean isDaysEarlier = lastTimeRefreshDateTime.toLocalDate().isBefore(now.toLocalDate());
        for (Time timeRefresh : listTimeRefresh) {
            if (timeNow.after(timeRefresh) && (isDaysEarlier || lastRefreshTime.before(timeRefresh))) {
                return true;
            }
        }
        return false;
    }

    private void updateSeaMapRefreshTransaction(SeaMapRefreshTransaction seaMapRefreshTransaction) throws JsonProcessingException {
        List<SeaElement> seaElements = seaElementService.getElementsActive();
        seaMapRefreshTransaction.setElementTransactionModels(seaMapTransactionMapper.mapToSeaMapRefreshTransaction(seaElements));
        seaMapRefreshTransaction.setSeaMapConfigTransactionsModels(seaMapTransactionMapper
                                                                     .mapToSeaElementConfigTransaction(elementsConfigService.getAllElementsConfig()));
    }
}

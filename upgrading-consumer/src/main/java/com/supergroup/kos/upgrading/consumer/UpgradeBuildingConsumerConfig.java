package com.supergroup.kos.upgrading.consumer;

import java.util.HashMap;
import java.util.Optional;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.CustomExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.supergroup.core.constant.ErrorCode;
import com.supergroup.core.exception.KOSException;
import com.supergroup.kos.building.domain.model.upgrade.InfoInstanceModel;
import com.supergroup.kos.building.domain.model.upgrade.UpgradeSession;
import com.supergroup.kos.building.domain.repository.persistence.upgrade.UpgradeSessionRepository;
import com.supergroup.kos.building.domain.service.ship.EscortShipService;
import com.supergroup.kos.building.domain.service.ship.MotherShipService;
import com.supergroup.kos.building.domain.service.upgrade.UpgradeService;
import com.supergroup.kos.building.domain.task.UpgradeTask;

import io.sentry.Sentry;

@Configuration
public class UpgradeBuildingConsumerConfig {

    @Autowired
    private ObjectMapper   objectMapper;
    @Autowired
    private UpgradeService upgradeService;

    @Autowired
    private MotherShipService        motherShipService;
    @Autowired
    private EscortShipService        escortShipService;
    @Autowired
    private UpgradeSessionRepository upgradeSessionRepository;

    @Bean
    CustomExchange upgradingExchange() {
        var args = new HashMap<String, Object>();
        args.put("x-delayed-type", "direct");
        return new CustomExchange("upgrading-exchange", "x-delayed-message", true, false, args);
    }

    @Bean
    Binding bindingUpgradeBuilding(@Qualifier("upgradingQueue") Queue queue,
                                   @Qualifier("upgradingExchange") CustomExchange exchange) {
        return BindingBuilder
                .bind(queue)
                .to(exchange)
                .with("upgrading")
                .noargs();
    }

    @Bean
    public Queue upgradingQueue() {
        return new Queue("upgrading");
    }

    @RabbitListener(queues = "upgrading")
    public void receive(String input) throws JsonProcessingException {
        try {
            var task = objectMapper.readValue(input, UpgradeTask.class);
            Optional<UpgradeSession> optional = upgradeSessionRepository.findById(task.getUpgradeSessionId());
            if (optional.isEmpty()) {
                return;
            }
            UpgradeSession upgradeSession = optional.get();
            if(upgradeSession.getIsDeleted()) {
                return;
            }
            InfoInstanceModel infoInstanceModel = upgradeSession.getInfoInstanceModel();
            switch (infoInstanceModel.getType()) {
                case BUILDING:
                    upgradeService.completeUpgradeBuilding(upgradeSession);
                    break;
                case MOTHER_SHIP:
                    motherShipService.completeUpgradeMotherShip(upgradeSession);
                    break;
                case ESCORT_SHIP:
                    escortShipService.completeUpgradeLevelEscortShip(upgradeSession);
                    break;
                case ESCORT_BUILDING:
                    escortShipService.completeBuildEscortShip(upgradeSession);
                    break;
                default:
                    throw KOSException.of(ErrorCode.BAD_REQUEST_ERROR);
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

package com.supergroup.kos.building.domain.service.seamap.activity.arrivalHandler;

import java.util.Objects;

import org.springframework.stereotype.Component;

import com.supergroup.kos.building.domain.async.OccupyCombatAsyncTask;
import com.supergroup.kos.building.domain.command.KosProfileCommand;
import com.supergroup.kos.building.domain.command.WithdrawActivityCommand;
import com.supergroup.kos.building.domain.constant.battle.BattleType;
import com.supergroup.kos.building.domain.model.asset.Assets;
import com.supergroup.kos.building.domain.model.asset.OccupiedBaseTax;
import com.supergroup.kos.building.domain.model.battle.Battle;
import com.supergroup.kos.building.domain.model.battle.BattleProfile;
import com.supergroup.kos.building.domain.model.profile.KosProfile;
import com.supergroup.kos.building.domain.model.seamap.Invader;
import com.supergroup.kos.building.domain.model.seamap.SeaActivity;
import com.supergroup.kos.building.domain.model.seamap.UserBase;
import com.supergroup.kos.building.domain.model.seamap.movesession.MoveSession;
import com.supergroup.kos.building.domain.model.seamap.reward.LoadedOnShipReward;
import com.supergroup.kos.building.domain.repository.persistence.seamap.SeaActivityRepository;
import com.supergroup.kos.building.domain.service.asset.AssetsService;
import com.supergroup.kos.building.domain.service.battle.BattlePvPService;
import com.supergroup.kos.building.domain.service.battle.OccupyService;
import com.supergroup.kos.building.domain.service.config.KosConfigService;
import com.supergroup.kos.building.domain.service.seamap.AllianceService;
import com.supergroup.kos.building.domain.service.seamap.activity.SeaActivityService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class OccupiedBaseEntryHandler implements MoveSessionHandler<UserBase> {
    private final SeaActivityService    seaActivityService;
    private final OccupyService         occupyService;
    private final BattlePvPService      battlePvPService;
    private final OccupyCombatAsyncTask occupyCombatAsyncTask;
    private final AllianceService       allianceService;
    private final AssetsService         assetsService;
    private final KosConfigService      configService;

    @Override
    public void handleMove(UserBase colonizedBase, MoveSession session, SeaActivity activity) {
        Battle battleOnTarget = colonizedBase.getBattle();
        KosProfile userKosProfile = activity.getKosProfile();
        if (occupyService.occupiedByAlliance(userKosProfile, colonizedBase)) {
            payTax(activity.getLoadedOnShipReward(), colonizedBase);
            seaActivityService.stationOnBase(activity, colonizedBase);
            occupyCombatAsyncTask.sendQueryInvaderForceNotification(colonizedBase.getKosProfile().getUser().getId());
            canJoinBattle(battleOnTarget, activity);
        } else {
            if (!canJoinBattle(battleOnTarget, activity)) {
                seaActivityService.withdraw(new WithdrawActivityCommand().setId(activity.getId()));
            }
        }
    }

    private boolean canJoinBattle(Battle battleOnTarget, SeaActivity activity) {
        if (Objects.isNull(battleOnTarget)) {return false;}
        KosProfile userKosProfile = activity.getKosProfile();
        BattleProfile attacker = battleOnTarget.getAttacker();
        BattleProfile defender = battleOnTarget.getDefender();
        if (allianceService.belongToSameAlliance(userKosProfile.getId(), attacker.getKosProfile().getId())) {
            battlePvPService.joinBattle(battleOnTarget, attacker, activity);
            return true;
        } else if (allianceService.belongToSameAlliance(userKosProfile.getId(), defender.getKosProfile().getId())) {
            battlePvPService.joinBattle(battleOnTarget, defender, activity);
            return true;
        }
        return false;
    }

    private void payTax(LoadedOnShipReward loadedOnShip, UserBase occupiedBase) {
        KosProfile occupiedBaseOwner = occupiedBase.getKosProfile();
        Double gold = loadedOnShip.getGold();
        Double stone = loadedOnShip.getStone();
        Double wood = loadedOnShip.getWood();
        Double taxOnOccupiedBase = configService.getTaxOnOccupiedBase();
        Double goldTax = taxOnOccupiedBase * gold;
        Double woodTax = taxOnOccupiedBase * wood;
        Double stoneTax = taxOnOccupiedBase * stone;

        Assets assets = assetsService.getAssets(new KosProfileCommand().setKosProfileId(occupiedBaseOwner.getId()));
        assets.setStone(assets.getStone() + stoneTax);
        assets.setWood(assets.getWood() + woodTax);
        assets.setGold(assets.getGold() + goldTax);

        loadedOnShip.setGold(gold - goldTax);
        loadedOnShip.setWood(wood - woodTax);
        loadedOnShip.setStone(stone - stoneTax);

        occupyCombatAsyncTask.sendOccupiedBaseTaxCharge(occupiedBase.getKosProfile().getUser().getId(),
                                                        loadedOnShip.getActivity().getKosProfile().getUser().getId(),
                                                        new OccupiedBaseTax().setGold((long) Math.ceil(goldTax))
                                                                             .setWood((long) Math.ceil(woodTax))
                                                                             .setStone((long) Math.ceil(stoneTax)));

    }
}

package com.supergroup.kos.building.domain.service.battle;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.supergroup.auth.domain.model.UserProfile;
import com.supergroup.auth.domain.service.UserProfileService;
import com.supergroup.core.constant.ErrorCode;
import com.supergroup.core.exception.KOSException;
import com.supergroup.kos.building.domain.constant.BattleProfileType;
import com.supergroup.kos.building.domain.constant.battle.FactionType;
import com.supergroup.kos.building.domain.model.battle.Battle;
import com.supergroup.kos.building.domain.model.battle.BattleProfile;
import com.supergroup.kos.building.domain.model.battle.BossSeaEmbedded;
import com.supergroup.kos.building.domain.model.profile.KosProfile;
import com.supergroup.kos.building.domain.model.seamap.BossSea;
import com.supergroup.kos.building.domain.repository.persistence.battle.BattleProfileRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BattleProfileService {
    private final BattleProfileRepository battleProfileRepository;
    private final UserProfileService      userProfileService;

    public BattleProfile save(BattleProfile battleProfile) {
        return battleProfileRepository.save(battleProfile);
    }

    public List<BattleProfile> saveAll(List<BattleProfile> battleProfiles) {
        return battleProfileRepository.saveAll(battleProfiles);
    }

    public BattleProfile createUserBattleProfile(KosProfile kosProfile, Battle battle, FactionType factionType) {
        UserProfile userProfile = userProfileService.findByUserId(kosProfile.getUser().getId());
        return new BattleProfile().setKosProfile(kosProfile)
                                  .setType(BattleProfileType.USER)
                                  .setBattle(battle)
                                  .setFaction(factionType)
                                  .setBattleReport(battle.getBattleReport())
                                  .setAvatar(userProfile.getAvatarUrl())
                                  .setCoordinates(kosProfile.getBase().getCoordinates())
                                  .setUsername(userProfile.getUsername());
    }

    public BattleProfile createBossBattleProfile(BossSea bossSea, Battle battle, FactionType factionType) {
        BossSeaEmbedded bossSeaEmbedded = new BossSeaEmbedded().setId(bossSea.getId())
                                                               .setConfigId(bossSea.getSeaElementConfig().getId());
        return new BattleProfile().setBossSea(bossSeaEmbedded)
                                  .setType(BattleProfileType.BOSS)
                                  .setBattle(battle)
                                  .setFaction(factionType)
                                  .setBattleReport(battle.getBattleReport())
                                  .setAvatar(bossSea.getSeaElementConfig().getThumbnail())
                                  .setCoordinates(bossSea.getCoordinates())
                                  .setUsername(bossSea.getSeaElementConfig().getName());
    }

    public BattleProfile findOrCreateBattleProfile(Battle battle, BattleProfile battleProfileAlly, KosProfile kosProfileJoin) {
        FactionType factionType = null;
        if (battle.getAttacker().getKosProfile().getId().equals(battleProfileAlly.getKosProfile().getId())) {
            factionType = FactionType.ATTACKER;
        } else if (battle.getDefender().getKosProfile().getId().equals(battleProfileAlly.getKosProfile().getId())) {
            factionType = FactionType.DEFENDER;
        } else {
            throw KOSException.of(ErrorCode.BAD_REQUEST_ERROR);
        }
        Optional<BattleProfile> optional = battleProfileRepository.findByBattleIdAndKosProfileIdAndFaction(battle.getId(), kosProfileJoin.getId(),
                                                                                                           factionType);
        if (optional.isPresent()) {
            return optional.get();
        } else {
            BattleProfile battleProfile = createUserBattleProfile(kosProfileJoin, battle, factionType);
            return battleProfileRepository.save(battleProfile);
        }

    }
}

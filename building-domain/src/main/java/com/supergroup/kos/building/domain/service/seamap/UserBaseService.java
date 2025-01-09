package com.supergroup.kos.building.domain.service.seamap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;

import org.apache.commons.lang3.SerializationUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.supergroup.core.constant.ErrorCode;
import com.supergroup.core.exception.KOSException;
import com.supergroup.kos.building.domain.command.SaveOrUpdateElementCommand;
import com.supergroup.kos.building.domain.constant.battle.BattleStatus;
import com.supergroup.kos.building.domain.model.battle.Battle;
import com.supergroup.kos.building.domain.model.battle.BattleProfile;
import com.supergroup.kos.building.domain.model.battle.OccupationBase;
import com.supergroup.kos.building.domain.model.seamap.Coordinates;
import com.supergroup.kos.building.domain.model.seamap.CoordinatesConflictNewBase;
import com.supergroup.kos.building.domain.model.seamap.OccupiedArea;
import com.supergroup.kos.building.domain.model.seamap.UserBase;
import com.supergroup.kos.building.domain.repository.persistence.seamap.SeaElementRepository;
import com.supergroup.kos.building.domain.repository.persistence.seamap.UserBaseRepository;
import com.supergroup.kos.building.domain.utils.SeaMapCoordinatesUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserBaseService {
    private final UserBaseRepository    userBaseRepository;
    private final SeaElementService     seaElementService;
    private final ElementsConfigService elementsConfigService;
    private final MapService            mapService;
    private final SeaElementRepository  seaElementRepository;

    public UserBase getByKosProfileId(Long kosProfileId) {
        return userBaseRepository.findUserBaseByKosProfileId(kosProfileId).orElseThrow(() -> new KOSException(ErrorCode.USER_BASE_NOT_FOUND));
    }
    public UserBase findByCoordinatesAndActive(Coordinates coordinates){
        List<UserBase> list = userBaseRepository.findByCoordinatesAndActive(coordinates, true);
        var size = list.size();
        if(size > 1) {
            log.info("Waring: many base in coordinates x:{}, y:{} ", coordinates.getX(), coordinates.getY());
        } else if (size == 1){
            return list.get(0);
        } else {
            return null;
        }
        return null;
    }

    public UserBase getById(Long id) {
        Optional<UserBase> nullable = userBaseRepository.findById(id);
        if (nullable.isEmpty()) {throw new KOSException(ErrorCode.USER_BASE_NOT_FOUND);}
        return nullable.get();
    }

    public UserBase getUserBaseFree() {
        return userBaseRepository.findFirstByActiveAndIsReadyAndKosProfileIsNullOrderByIdAsc(false, true).orElseThrow(
                () -> new KOSException(ErrorCode.CAN_NOT_FIND_EMPTY_USER_BASE));
    }
    public UserBase getUserBaseFreeRandom() {
        List<UserBase> listUserBase = userBaseRepository.findByActiveAndIsReady(false, true);
        if(listUserBase.isEmpty()) {
            throw new KOSException(ErrorCode.CAN_NOT_FIND_EMPTY_USER_BASE);
        }
        Integer indexRandom = new Random().nextInt(listUserBase.size());
        return listUserBase.get(indexRandom);
    }

    public Boolean existUserBase(Long kosProfileId) {
        Optional<UserBase> optional = userBaseRepository.findUserBaseByKosProfileId(kosProfileId);
        return optional.isPresent();
    }

    @Transactional
    public Coordinates moveBaseUserRandom(Long kosProfileId) {
        UserBase currentUserBase = getByKosProfileId(kosProfileId);
        UserBase newBase = getUserBaseFreeRandom();
        swapBaseUser(currentUserBase, newBase);
        mapService.saveOrUpdateElement(new SaveOrUpdateElementCommand(newBase));
        return newBase.getCoordinates();
    }

    @Transactional
    public void moveBaseUserCertain(Long kosProfileId, Coordinates coordinates) {
        List<CoordinatesConflictNewBase> conflictNewBases = seaElementRepository.findCoordinatesConflictNewBase(coordinates.getX(), coordinates.getY());
        List<Long> idsBaseConflict = new ArrayList<>();
        for(CoordinatesConflictNewBase conflictNewBase: conflictNewBases) {
            if(Objects.equals(conflictNewBase.getType(), "USER") && !conflictNewBase.getActive()) {
                idsBaseConflict.add(conflictNewBase.getId());
            } else {
                throw KOSException.of(ErrorCode.INVALID_COORDINATES);
            }
        }
        UserBase currentUserBase = getByKosProfileId(kosProfileId);
        List<UserBase> userBaseListFreeConflict = userBaseRepository.findByIdIn(idsBaseConflict);
        if (userBaseListFreeConflict.size() == 1) {
            UserBase userBase = userBaseListFreeConflict.get(0);
            Coordinates coordinates1 = userBase.getCoordinates();
            if (coordinates1.getX().equals(coordinates.getX()) && coordinates1.getY().equals(coordinates.getY())) {
                swapBaseUser(currentUserBase, userBaseListFreeConflict.get(0));
            } else {
                currentUserBase.setCoordinate(coordinates);
                mapService.saveOrUpdateElement(new SaveOrUpdateElementCommand(currentUserBase));
                userBase.setIsReady(false);
                mapService.saveOrUpdateElement(new SaveOrUpdateElementCommand(userBase));
            }
        } else {
            currentUserBase.setCoordinate(coordinates);
            mapService.saveOrUpdateElement(new SaveOrUpdateElementCommand(currentUserBase));
            for (UserBase userBase : userBaseListFreeConflict) {
                userBase.setIsReady(false);
                mapService.saveOrUpdateElement(new SaveOrUpdateElementCommand(userBase));
            }
        }
    }

    private String ToStringCoordinates(Long x, Long y) {
        return "(" + x + ";" + y + ")";
    }

    private Coordinates stringToCoordinates(String s) {
        List<String> list = List.of(s.substring(1, s.length() - 1).split("\\;"));
        return new Coordinates(Long.valueOf(list.get(0)), Long.valueOf(list.get(1)));
    }

    private String ToStringCoordinatesWithStep(String s, Long stepX, Long stepY) {
        List<String> list = List.of(s.substring(1, s.length() - 1).split("\\;"));
        return ToStringCoordinates(Long.parseLong(list.get(0)) + stepX, Long.parseLong(list.get(1)) + stepY);
    }

    public List<Coordinates> getListCoordinates(String s) {
        List<String> list = List.of(s.substring(1, s.length() - 1).replace(" ", "").split("\\,"));
        List<Coordinates> response = new ArrayList<>();
        for (String s1 : list) {
            response.add(stringToCoordinates(s1));
        }
        return response;
    }

    private Boolean existCoordinates(Coordinates coordinates, OccupiedArea occupiedArea,
                                     Map<String, String> map) {
        Long width = occupiedArea.getWidth();
        Long height = occupiedArea.getHeight();
        for (long i = 0; i < width; i++) {
            for (long j = 0; j < height; j++) {
                if (map.containsKey(ToStringCoordinates(coordinates.getX().longValue() + i, coordinates.getY().longValue() + j))) {
                    return true;
                }
            }
        }
        return false;
    }

    private Coordinates swapBaseUser(UserBase currentUserBase, UserBase newBase) {
        newBase.setKosProfile(SerializationUtils.clone(currentUserBase.getKosProfile()))
               .setIslandName(SerializationUtils.clone(currentUserBase.getIslandName()))
               .setActive(true);
        currentUserBase.setKosProfile(null)
                       .setIslandName(null)
                       .setActive(false);

        mapService.saveOrUpdateElement(new SaveOrUpdateElementCommand(currentUserBase));
        mapService.saveOrUpdateElement(new SaveOrUpdateElementCommand(newBase));
        return newBase.getCoordinates();
    }

    private Boolean check(String npcElements, Map<String, String> map) {
        List<Coordinates> coordinatesList = SeaMapCoordinatesUtils.stringToListCoordinates(npcElements);
        for(Coordinates coordinates : coordinatesList) {
            if(map.containsKey(SeaMapCoordinatesUtils.toStringCoordinates(coordinates.getX(), coordinates.getY()))) {
                return true;
            }
        }
        return false;
    }

    public List<OccupationBase> getListOccupations(Long kosProfileId) {
        return userBaseRepository.getListOccupations(kosProfileId);
    }

    public BattleProfile fightingOnMyBase(Long kosProfileId) {
        Optional<UserBase> optional = userBaseRepository.findUserBaseByKosProfileId(kosProfileId);
        if(optional.isPresent() && Objects.nonNull(optional.get().getBattle())) {
            Battle battle = optional.get().getBattle();
            if(BattleStatus.getStatusActive().contains(battle.getStatus())) {
                if(battle.getAttacker().getKosProfile().getId().equals(kosProfileId)) {
                    return battle.getAttacker();
                }
                if(battle.getDefender().getKosProfile().getId().equals(kosProfileId)) {
                    return battle.getDefender();
                }
            }
        }
        return null;
    }
}

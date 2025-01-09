package com.supergroup.kos.building.domain.constant.seamap;

/* legacy code , remove in sp7*/
public enum MoveSessionType {
    BOSS_BATTLE(Constants.BOSS_BATTLE),
    USER_BATTLE(Constants.USER_BATTLE),
    MINING(Constants.MINING),
    ANCHOR(Constants.ANCHOR),
    RETURN_BASE(Constants.RETURN_BASE),
    SCOUT(Constants.SCOUT),
    RETURN_COLONIZED_BASE(Constants.RETURN_COLONIZED_BASE);

    MoveSessionType(String type) {
    }

    public static class Constants {
        public static final String BOSS_BATTLE           = "BOSS_BATTLE";
        public static final String USER_BATTLE           = "USER_BATTLE";
        public static final String MINING                = "MINING";
        public static final String ANCHOR                = "ANCHOR";
        public static final String RETURN_BASE           = "RETURN_BASE";
        public static final String RETURN_COLONIZED_BASE = "RETURN_COLONIZED_BASE";
        public static final String SCOUT                 = "SCOUT";

    }
}

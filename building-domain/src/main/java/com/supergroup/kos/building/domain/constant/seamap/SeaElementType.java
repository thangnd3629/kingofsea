package com.supergroup.kos.building.domain.constant.seamap;

public enum SeaElementType {

    BOSS(SeaElementType.Constants.NPC),
    USER_BASE(SeaElementType.Constants.USER_BASE),
    RESOURCE(SeaElementType.Constants.RESOURCE),
    SHIP(SeaElementType.Constants.SHIP),
    OCCUPIED_ENEMY_BASE(SeaElementType.Constants.OCCUPIED_ENEMY_BASE),
    OCCUPIED_OWNED_BASE(SeaElementType.Constants.OCCUPIED_OWNED_BASE),
    STATIONED_ENEMY_BASE(SeaElementType.Constants.STATIONED_ENEMY_BASE);

    SeaElementType(String type) {
    }

    public static class Constants {
        public static final String USER_BASE = "USER_BASE";
        public static final String NPC       = "NPC";
        public static final String RESOURCE  = "RESOURCE";
        public static final String SHIP      = "SHIP";

        public static final String OCCUPIED_ENEMY_BASE = "OCCUPIED_ENEMY_BASE";
        public static final String OCCUPIED_OWNED_BASE = "OCCUPIED_OWNED_BASE";
        public static final String STATIONED_ENEMY_BASE = "STATIONED_ENEMY_BASE";
    }
}

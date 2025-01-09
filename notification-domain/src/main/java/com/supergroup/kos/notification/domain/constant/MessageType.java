package com.supergroup.kos.notification.domain.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MessageType {
    AUTH_LOGGED("AUTH:LOGGED", "Authentication"),
    BUILDING_UPGRADE("BUILDING:UPGRADE", "Upgrading"),
    SCOUT("SCOUT", "Scout"),
    TOWN_NOTIFICATION_NEW("NOTIFICATION:TOWN:NEW", "Town Notification"),
    MAIL_NOTIFICATION_NEW("NOTIFICATION:MAIL:NEW", "Mail Notification"),
    NOTIFICATION_NEW("NOTIFICATION:NEW", "New notification"),
    COMBAT_BE_ATTACKED("COMBAT:BE_ATTACKED", "Be attacked"),
    MOVE_SESSION_CANCEL("MOVE_SESSION:CANCEL", "Move session is canceled"),
    COMBAT_CANCEL("COMBAT:CANCEL", "Cancel battle"),
    COMBAT_END("COMBAT:END", "Combat end");
    private String intent;
    private String title;
}

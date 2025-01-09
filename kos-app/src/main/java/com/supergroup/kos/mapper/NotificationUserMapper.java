package com.supergroup.kos.mapper;

import java.util.List;
import java.util.Objects;

import org.json.JSONArray;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;

import com.supergroup.kos.dto.notification.NotificationDTO;
import com.supergroup.kos.notification.domain.model.UserNotification;

@Mapper
public interface NotificationUserMapper {
    @Mappings({
            @Mapping(source = "notification.title", target = "title"),
            @Mapping(source = "notification.body", target = "body"),
            @Mapping(source = "notification.renderContents", target = "renderContents", qualifiedByName = "renderContentsMapper"),
            @Mapping(source = "notification.actions", target = "actions", qualifiedByName = "actionsMapper")

    })
    NotificationDTO toDTO(UserNotification userNotification);

    List<NotificationDTO> toDTOs(List<UserNotification> userNotifications);

    @Named("renderContentsMapper")
    default List<Object> toMap(JSONArray data) {
        return data.toList();
    }
    @Named("actionsMapper")
    default List<Object> toActions(String data){
        if (Objects.isNull(data)) return null;
        JSONArray jsonData = new JSONArray(data);
        return jsonData.toList();
    }
}

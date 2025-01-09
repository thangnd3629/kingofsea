package com.supergroup.kos.notification.domain.service;

import static com.supergroup.core.constant.NotificationRenderContentPlaceHolder.THUMBNAIL;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.supergroup.asset.service.AssetService;
import com.supergroup.core.constant.ErrorCode;
import com.supergroup.core.exception.KOSException;
import com.supergroup.core.utils.NamedParameterInterpolation;
import com.supergroup.kos.notification.domain.constant.RenderSection;
import com.supergroup.kos.notification.domain.model.Notification;
import com.supergroup.kos.notification.domain.model.NotificationTemplate;
import com.supergroup.kos.notification.domain.model.NotificationTemplateType;
import com.supergroup.kos.notification.domain.repository.NotificationTemplateRepository;
import com.supergroup.notification.service.DataMessage;
import com.supergroup.notification.service.NotificationMessage;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class NotificationTemplateService {
    private final NotificationTemplateRepository persistenceRepository;
    private final NotificationService notificationService;
    private final AssetService assetService;

    @Cacheable(cacheNames = "NotificationTemplate", key = "#type.toString()")
    public NotificationTemplate getByTemplateType(NotificationTemplateType type) {
        Optional<NotificationTemplate> template = persistenceRepository.findByTemplateType(type);
        if (template.isPresent()) {
            return template.get();
        }
        throw KOSException.of(ErrorCode.NOTIFICATION_TEMPLATE_NOT_FOUND);
    }

    public void sendByTemplate(Long userId, NotificationTemplateType type, Map<String, Object> namedParams, List<Object> intents,
                               List<RenderSection> renderSections) {
        try {
            NotificationTemplate template = getByTemplateType(type);
            var notiMessageTitle = template.getSystemTrayTitle();
            var notiMessageDetail = template.getSystemTrayDetail();
            var dataMesasgeDetail = template.getBannerDetail();
            var persistentMessageTitle = template.getPersistentTitle();
            var persistentMessageDetail = template.getPersistentDetail();
            var persistentMessageActions = template.getActions();
            notiMessageTitle = NamedParameterInterpolation.format(notiMessageTitle, namedParams, true);
            notiMessageDetail = NamedParameterInterpolation.format(notiMessageDetail, namedParams, true);
            dataMesasgeDetail = NamedParameterInterpolation.format(dataMesasgeDetail, namedParams, true);
            persistentMessageTitle = NamedParameterInterpolation.format(persistentMessageTitle, namedParams, true);
            persistentMessageDetail = NamedParameterInterpolation.format(persistentMessageDetail, namedParams, true);
            persistentMessageActions = NamedParameterInterpolation.format(persistentMessageActions, namedParams, true);

            JSONArray predefinedRenderContents = template.getRenderContents() != null ? new JSONArray(template.getRenderContents().toString()) :
                    new JSONArray();
            if (Objects.nonNull(renderSections)) {
                predefinedRenderContents = predefinedRenderContents.putAll(new JSONArray(renderSections));
            }

            for (int i = 0; i < predefinedRenderContents.length(); i++) {
                JSONArray renderContentResults = new JSONArray();
                JSONArray renderContentList = predefinedRenderContents.getJSONObject(i).getJSONArray("content");
                for (int j = 0; j < renderContentList.length(); j++) {
                    try {
                        String formattedRenderContent = NamedParameterInterpolation.format(renderContentList.getJSONObject(j).toString(), namedParams,
                                false);
                        if (Objects.isNull(formattedRenderContent)) {
                            continue;
                        }
                        JSONObject jsonFormattedRenderContent = new JSONObject(formattedRenderContent);
                        jsonFormattedRenderContent.put(THUMBNAIL, assetService.getUrl(jsonFormattedRenderContent.getString(THUMBNAIL)));
                        renderContentResults.put(jsonFormattedRenderContent);
                    } catch (KOSException e) {
                        if (!e.getCode().equals(ErrorCode.NOTIFICATION_NAMED_PARAM_MISSING)) {
                            throw e;
                        }
                    }
                }
                predefinedRenderContents.getJSONObject(i).put("content", renderContentResults);
            }
            var notificationMessage = new NotificationMessage()
                    .setBody(notiMessageDetail)
                    .setTitle(notiMessageTitle);
            var dataMessage = new DataMessage()
                    .setBody(dataMesasgeDetail)
                    .setIsPersistent(Objects.nonNull(persistentMessageTitle))
                    .setIntents(intents);
            var persistentMessage = new Notification()
                    .setBody(persistentMessageDetail)
                    .setTitle(persistentMessageTitle)
                    .setRenderContents(predefinedRenderContents)
                    .setActions(persistentMessageActions);
            if (Objects.isNull(notiMessageTitle) && Objects.isNull(notiMessageDetail)) {
                notificationMessage = null;
            }
            if (Objects.isNull(dataMesasgeDetail)) {
                dataMessage = null;
            }
            if (Objects.isNull(persistentMessageTitle) && Objects.isNull(persistentMessageDetail)) {
                persistentMessage = null;
            }
            notificationService.sendDirect(userId, notificationMessage, dataMessage, persistentMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

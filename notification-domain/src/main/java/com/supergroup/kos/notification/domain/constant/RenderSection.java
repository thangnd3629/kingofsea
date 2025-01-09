package com.supergroup.kos.notification.domain.constant;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(chain = true)
public class RenderSection {
    private String              section;
    private List<RenderContent> content = new ArrayList<>();
    private Style               style   = Style.LIST;

    public enum Style {
        THUMBNAIL, LIST
    }

    @Getter
    @Setter
    @Accessors(chain = true)
    public static class RenderContent {
        private String thumbnail;
        private String title;
        private String value;
    }
}

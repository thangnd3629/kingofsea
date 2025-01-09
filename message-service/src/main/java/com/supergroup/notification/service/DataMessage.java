package com.supergroup.notification.service;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class DataMessage {
    private String        body;
    private List<Object> intents = new ArrayList<>();
    private Boolean       isPersistent = false;
}

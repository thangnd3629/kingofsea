package com.supergroup.email.service;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum EmailType {

    HTML("text/html");

    private String type;
}

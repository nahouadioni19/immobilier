package com.app.utils;

import org.springframework.stereotype.Component;

@Component("stringUtils")
public class StringUtils {

    public String truncate(String text, int max) {
        if (text == null) return "";
        return text.length() > max ? text.substring(0, max) + "..." : text;
    }
}
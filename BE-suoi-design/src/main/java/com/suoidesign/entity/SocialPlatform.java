package com.suoidesign.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SocialPlatform {
    TIKTOK("tiktok"),
    FACEBOOK("facebook"),
    INSTAGRAM("instagram");
    
    private final String platformName;
}

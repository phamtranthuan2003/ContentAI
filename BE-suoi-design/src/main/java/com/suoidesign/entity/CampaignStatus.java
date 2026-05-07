package com.suoidesign.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CampaignStatus {
    DRAFT("Draft"),
    SCHEDULED("Scheduled"),
    PUBLISHED("Published"),
    FAILED("Failed");
    
    private final String statusName;
}

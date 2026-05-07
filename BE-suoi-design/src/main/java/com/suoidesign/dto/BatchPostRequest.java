package com.suoidesign.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BatchPostRequest {
    
    @NotNull(message = "Campaign ID is required")
    private Long campaignId;
    
    @NotNull(message = "Content is required")
    private String content;
    
    private String mediaUrl;
    
    @NotEmpty(message = "At least one account must be selected")
    private List<Long> selectedAccountIds;
    
    private LocalDateTime scheduledTime;
}

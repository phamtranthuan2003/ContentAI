package com.suoidesign.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BatchPostResult {
    
    private List<PostResult> results;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PostResult {
        private Long accountId;
        private String accountName;
        private Boolean success;
        private String message;
        private String postId;
    }
}

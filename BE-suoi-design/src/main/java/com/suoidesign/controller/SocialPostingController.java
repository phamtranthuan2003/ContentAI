package com.suoidesign.controller;

import com.suoidesign.dto.BatchPostRequest;
import com.suoidesign.dto.BatchPostResult;
import com.suoidesign.service.SocialPostingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/social")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SocialPostingController {
    
    private final SocialPostingService socialPostingService;
    
    @PostMapping("/batch-post")
    public ResponseEntity<BatchPostResult> batchPost(
            @Valid @RequestBody BatchPostRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Long userId = getUserIdFromPrincipal(userDetails);
        BatchPostResult result = socialPostingService.batchPost(userId, request);
        return ResponseEntity.ok(result);
    }
    
    private Long getUserIdFromPrincipal(UserDetails userDetails) {
        // Extract user ID from JWT token or session
        // For now, return a placeholder - implement based on your authentication mechanism
        return 1L;
    }
}

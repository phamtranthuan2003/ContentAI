package com.suoidesign.controller;

import com.suoidesign.entity.SocialAccount;
import com.suoidesign.service.SocialAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/oauth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class OAuthController {
    
    private final SocialAccountService socialAccountService;
    
    @GetMapping("/accounts")
    public ResponseEntity<List<SocialAccount>> getUserAccounts(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = getUserIdFromPrincipal(userDetails);
        List<SocialAccount> accounts = socialAccountService.getUserAccounts(userId);
        return ResponseEntity.ok(accounts);
    }
    
    @DeleteMapping("/accounts/{id}")
    public ResponseEntity<Void> removeAccount(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Long userId = getUserIdFromPrincipal(userDetails);
        socialAccountService.deleteAccount(id, userId);
        return ResponseEntity.noContent().build();
    }
    
    private Long getUserIdFromPrincipal(UserDetails userDetails) {
        // Extract user ID from JWT token or session
        // For now, return a placeholder - implement based on your authentication mechanism
        return 1L;
    }
}

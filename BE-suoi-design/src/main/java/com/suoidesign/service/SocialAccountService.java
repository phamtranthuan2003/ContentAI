package com.suoidesign.service;

import com.suoidesign.entity.SocialAccount;
import com.suoidesign.entity.SocialPlatform;
import com.suoidesign.repository.SocialAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SocialAccountService {
    
    private final SocialAccountRepository socialAccountRepository;
    
    public List<SocialAccount> getUserAccounts(Long userId) {
        return socialAccountRepository.findByUserIdAndIsActiveTrue(userId);
    }
    
    public List<SocialAccount> getUserAccountsByPlatform(Long userId, SocialPlatform platform) {
        return socialAccountRepository.findByUserIdAndPlatform(userId, platform);
    }
    
    public SocialAccount saveAccount(SocialAccount account) {
        return socialAccountRepository.save(account);
    }
    
    @Transactional
    public void deleteAccount(Long accountId, Long userId) {
        SocialAccount account = socialAccountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        
        if (!account.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized access to account");
        }
        
        account.setIsActive(false);
        socialAccountRepository.save(account);
    }
    
    public SocialAccount updateAccount(Long accountId, Long userId, SocialAccount updatedAccount) {
        SocialAccount account = socialAccountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        
        if (!account.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized access to account");
        }
        
        account.setAccessToken(updatedAccount.getAccessToken());
        account.setRefreshToken(updatedAccount.getRefreshToken());
        account.setTokenExpiresAt(updatedAccount.getTokenExpiresAt());
        account.setUpdatedAt(LocalDateTime.now());
        
        return socialAccountRepository.save(account);
    }
}

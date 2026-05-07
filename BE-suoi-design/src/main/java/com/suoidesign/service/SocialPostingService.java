package com.suoidesign.service;

import com.suoidesign.dto.BatchPostRequest;
import com.suoidesign.dto.BatchPostResult;
import com.suoidesign.entity.SocialAccount;
import com.suoidesign.entity.SocialPlatform;
import com.suoidesign.repository.SocialAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SocialPostingService {
    
    private final SocialAccountRepository socialAccountRepository;
    private final TikTokPostingService tikTokPostingService;
    private final FacebookPostingService facebookPostingService;
    private final InstagramPostingService instagramPostingService;
    
    public BatchPostResult batchPost(Long userId, BatchPostRequest request) {
        BatchPostResult result = new BatchPostResult();
        List<BatchPostResult.PostResult> postResults = new ArrayList<>();
        
        for (Long accountId : request.getSelectedAccountIds()) {
            SocialAccount account = socialAccountRepository.findById(accountId)
                    .orElseThrow(() -> new RuntimeException("Account not found"));
            
            if (!account.getUserId().equals(userId)) {
                throw new RuntimeException("Unauthorized access to account");
            }
            
            if (!account.getIsActive()) {
                postResults.add(new BatchPostResult.PostResult(
                        accountId, account.getAccountName(), false, "Account is deactivated", null
                ));
                continue;
            }
            
            try {
                BatchPostResult.PostResult postResult;
                switch (account.getPlatform()) {
                    case TIKTOK:
                        postResult = tikTokPostingService.postVideo(account, request);
                        break;
                    case FACEBOOK:
                        postResult = facebookPostingService.postContent(account, request);
                        break;
                    case INSTAGRAM:
                        postResult = instagramPostingService.postContent(account, request);
                        break;
                    default:
                        throw new RuntimeException("Unsupported platform");
                }
                postResults.add(postResult);
            } catch (Exception e) {
                postResults.add(new BatchPostResult.PostResult(
                        accountId, account.getAccountName(), false, e.getMessage(), null
                ));
            }
        }
        
        result.setResults(postResults);
        return result;
    }
}

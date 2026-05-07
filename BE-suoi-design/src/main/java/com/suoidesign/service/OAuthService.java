package com.suoidesign.service;

import com.suoidesign.entity.SocialAccount;
import com.suoidesign.entity.SocialPlatform;
import com.suoidesign.repository.SocialAccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OAuthService {
    
    private final SocialAccountRepository socialAccountRepository;
    private final RestTemplate restTemplate;
    
    @Value("${tiktok.client-id}")
    private String tiktokClientId;
    
    @Value("${tiktok.client-secret}")
    private String tiktokClientSecret;
    
    @Value("${tiktok.redirect-uri}")
    private String tiktokRedirectUri;
    
    @Value("${tiktok.auth-url}")
    private String tiktokAuthUrl;
    
    @Value("${tiktok.token-url}")
    private String tiktokTokenUrl;
    
    public String getAuthorizationUrl(SocialPlatform platform, String redirectUri) {
        switch (platform) {
            case TIKTOK:
                return getTikTokAuthUrl(redirectUri);
            case FACEBOOK:
                return getFacebookAuthUrl(redirectUri);
            case INSTAGRAM:
                return getInstagramAuthUrl(redirectUri);
            default:
                throw new RuntimeException("Unsupported platform: " + platform);
        }
    }
    
    private String getTikTokAuthUrl(String redirectUri) {
        String state = UUID.randomUUID().toString();
        return String.format("%s?client_key=%s&scope=user.info.basic,video.create&response_type=code&redirect_uri=%s&state=%s",
                tiktokAuthUrl, tiktokClientId, redirectUri, state);
    }
    
    private String getFacebookAuthUrl(String redirectUri) {
        String state = UUID.randomUUID().toString();
        return String.format("https://www.facebook.com/v18.0/dialog/oauth?client_id=%s&redirect_uri=%s&scope=pages_read_engagement,pages_manage_posts&response_type=code&state=%s",
                "${FACEBOOK_CLIENT_ID}", redirectUri, state);
    }
    
    private String getInstagramAuthUrl(String redirectUri) {
        String state = UUID.randomUUID().toString();
        return String.format("https://api.instagram.com/oauth/authorize?client_id=%s&redirect_uri=%s&scope=instagram_basic,instagram_content_publish&response_type=code&state=%s",
                "${INSTAGRAM_CLIENT_ID}", redirectUri, state);
    }
    
    public SocialAccount handleCallback(SocialPlatform platform, String code, Long userId) {
        switch (platform) {
            case TIKTOK:
                return handleTikTokCallback(code, userId);
            case FACEBOOK:
                return handleFacebookCallback(code, userId);
            case INSTAGRAM:
                return handleInstagramCallback(code, userId);
            default:
                throw new RuntimeException("Unsupported platform: " + platform);
        }
    }
    
    private SocialAccount handleTikTokCallback(String code, Long userId) {
        // Exchange code for access token
        Map<String, Object> tokenResponse = exchangeTikTokCodeForToken(code);
        
        // Get user info
        Map<String, Object> userInfo = getTikTokUserInfo((String) tokenResponse.get("access_token"));
        
        // Save account
        SocialAccount account = new SocialAccount();
        account.setUserId(userId);
        account.setPlatform(SocialPlatform.TIKTOK);
        account.setPlatformAccountId((String) userInfo.get("open_id"));
        account.setAccountName((String) userInfo.get("username"));
        account.setAccessToken((String) tokenResponse.get("access_token"));
        account.setRefreshToken((String) tokenResponse.get("refresh_token"));
        account.setTokenExpiresAt(LocalDateTime.now().plusSeconds((Integer) tokenResponse.get("expires_in")));
        account.setIsActive(true);
        
        return socialAccountRepository.save(account);
    }
    
    private SocialAccount handleFacebookCallback(String code, Long userId) {
        // TODO: Implement Facebook OAuth callback
        log.info("Handling Facebook OAuth callback for user: {}", userId);
        throw new RuntimeException("Facebook OAuth callback not yet implemented");
    }
    
    private SocialAccount handleInstagramCallback(String code, Long userId) {
        // TODO: Implement Instagram OAuth callback
        log.info("Handling Instagram OAuth callback for user: {}", userId);
        throw new RuntimeException("Instagram OAuth callback not yet implemented");
    }
    
    private Map<String, Object> exchangeTikTokCodeForToken(String code) {
        // TODO: Implement TikTok token exchange
        // POST to https://open.tiktokapis.com/v2/oauth/token/
        log.info("Exchanging TikTok code for token");
        throw new RuntimeException("TikTok token exchange not yet implemented");
    }
    
    private Map<String, Object> getTikTokUserInfo(String accessToken) {
        // TODO: Implement TikTok user info retrieval
        // GET from https://open.tiktokapis.com/v2/user/info/
        log.info("Getting TikTok user info");
        throw new RuntimeException("TikTok user info retrieval not yet implemented");
    }
}

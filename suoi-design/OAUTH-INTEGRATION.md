# OAuth 2.0 Integration Guide for Social Media Platforms

This guide explains how to integrate OAuth 2.0 authentication for TikTok, Facebook, and Instagram in your Java Spring Boot backend.

## Overview

The frontend UI in `admin/social.html` includes:
- Multiple account connections per platform
- Add/Remove account functionality
- Batch posting with account selection
- Modal for OAuth connection

## Backend Architecture

### Required Dependencies (pom.xml)

```xml
<dependencies>
    <!-- Spring Security OAuth2 Client -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-oauth2-client</artifactId>
    </dependency>
    
    <!-- Spring Web -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    
    <!-- Spring Security -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>
    
    <!-- HTTP Client for API calls -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-webflux</artifactId>
    </dependency>
</dependencies>
```

### Application Configuration (application.yml)

```yaml
spring:
  security:
    oauth2:
      client:
        registration:
          facebook:
            client-id: ${FACEBOOK_CLIENT_ID}
            client-secret: ${FACEBOOK_CLIENT_SECRET}
            scope:
              - pages_read_engagement
              - pages_manage_posts
              - pages_manage_metadata
            redirect-uri: "{baseUrl}/login/oauth2/code/{registrationId}"
            authorization-grant-type: authorization_code
          instagram:
            client-id: ${INSTAGRAM_CLIENT_ID}
            client-secret: ${INSTAGRAM_CLIENT_SECRET}
            scope:
              - instagram_basic
              - instagram_content_publish
            redirect-uri: "{baseUrl}/login/oauth2/code/{registrationId}"
            authorization-grant-type: authorization_code
        provider:
          facebook:
            authorization-uri: https://www.facebook.com/v18.0/dialog/oauth
            token-uri: https://graph.facebook.com/v18.0/oauth/access_token
            user-info-uri: https://graph.facebook.com/me
            user-name-attribute: id
          instagram:
            authorization-uri: https://api.instagram.com/oauth/authorize
            token-uri: https://api.instagram.com/oauth/access_token
            user-info-uri: https://graph.instagram.com/me
            user-name-attribute: id

# TikTok requires custom implementation (not in standard OAuth2 provider)
tiktok:
  client-id: ${TIKTOK_CLIENT_ID}
  client-secret: ${TIKTOK_CLIENT_SECRET}
  redirect-uri: ${TIKTOK_REDIRECT_URI}
```

## Database Schema

### SocialAccount Entity

```sql
CREATE TABLE social_accounts (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    platform VARCHAR(20) NOT NULL, -- 'tiktok', 'facebook', 'instagram'
    platform_account_id VARCHAR(255) NOT NULL,
    account_name VARCHAR(255),
    access_token TEXT NOT NULL,
    refresh_token TEXT,
    token_expires_at TIMESTAMP,
    profile_data JSON,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
```

### Java Entity Class

```java
@Entity
@Table(name = "social_accounts")
public class SocialAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id")
    private Long userId;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "platform")
    private SocialPlatform platform;
    
    @Column(name = "platform_account_id")
    private String platformAccountId;
    
    @Column(name = "account_name")
    private String accountName;
    
    @Column(name = "access_token", columnDefinition = "TEXT")
    private String accessToken;
    
    @Column(name = "refresh_token", columnDefinition = "TEXT")
    private String refreshToken;
    
    @Column(name = "token_expires_at")
    private LocalDateTime tokenExpiresAt;
    
    @Column(name = "profile_data", columnDefinition = "JSON")
    private String profileData;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Getters and Setters
}

public enum SocialPlatform {
    TIKTOK, FACEBOOK, INSTAGRAM
}
```

## API Endpoints

### 1. OAuth Connection Endpoints

```java
@RestController
@RequestMapping("/api/oauth")
public class OAuthController {
    
    @GetMapping("/connect/{platform}")
    public ResponseEntity<String> initiateOAuth(
            @PathVariable String platform,
            @RequestParam String redirectUri
    ) {
        // Generate OAuth authorization URL
        String authUrl = oauthService.getAuthorizationUrl(platform, redirectUri);
        return ResponseEntity.ok(authUrl);
    }
    
    @GetMapping("/callback/{platform}")
    public ResponseEntity<SocialAccount> handleOAuthCallback(
            @PathVariable String platform,
            @RequestParam String code,
            @RequestParam(required = false) String state
    ) {
        // Exchange code for access token and save account
        SocialAccount account = oauthService.handleCallback(platform, code, state);
        return ResponseEntity.ok(account);
    }
    
    @GetMapping("/accounts")
    public ResponseEntity<List<SocialAccount>> getUserAccounts(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Long userId = getUserIdFromPrincipal(userDetails);
        List<SocialAccount> accounts = socialAccountService.findByUserId(userId);
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
}
```

### 2. Batch Posting Endpoint

```java
@RestController
@RequestMapping("/api/social")
public class SocialPostingController {
    
    @PostMapping("/batch-post")
    public ResponseEntity<BatchPostResult> batchPost(
            @RequestBody BatchPostRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Long userId = getUserIdFromPrincipal(userDetails);
        BatchPostResult result = socialPostingService.batchPost(userId, request);
        return ResponseEntity.ok(result);
    }
}

public class BatchPostRequest {
    private Long campaignId;
    private String content;
    private String mediaUrl;
    private List<Long> selectedAccountIds;
    private LocalDateTime scheduledTime;
    // Getters and Setters
}
```

## Platform-Specific Integration

### TikTok Integration

TikTok doesn't have a standard OAuth2 provider in Spring Security, so you need custom implementation:

```java
@Service
public class TikTokOAuthService {
    
    private static final String AUTH_URL = "https://www.tiktok.com/v2/auth/authorize/";
    private static final String TOKEN_URL = "https://open.tiktokapis.com/v2/oauth/token/";
    
    public String getAuthorizationUrl(String redirectUri) {
        return UriComponentsBuilder.fromHttpUrl(AUTH_URL)
                .queryParam("client_key", tiktokClientId)
                .queryParam("scope", "user.info.basic,video.create")
                .queryParam("response_type", "code")
                .queryParam("redirect_uri", redirectUri)
                .queryParam("state", generateState())
                .toUriString();
    }
    
    public SocialAccount handleCallback(String code, String state) {
        // Exchange code for access token
        Map<String, String> tokenResponse = exchangeCodeForToken(code);
        
        // Get user info
        Map<String, Object> userInfo = getUserInfo(tokenResponse.get("access_token"));
        
        // Save to database
        SocialAccount account = new SocialAccount();
        account.setPlatform(SocialPlatform.TIKTOK);
        account.setAccessToken(tokenResponse.get("access_token"));
        account.setRefreshToken(tokenResponse.get("refresh_token"));
        account.setPlatformAccountId((String) userInfo.get("open_id"));
        account.setAccountName((String) userInfo.get("username"));
        // ... set other fields
        
        return socialAccountRepository.save(account);
    }
}
```

### Facebook Integration

```java
@Service
public class FacebookOAuthService {
    
    public SocialAccount handleCallback(String code, String state) {
        // Spring Security OAuth2 handles token exchange automatically
        OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(
            "facebook", 
            SecurityContextHolder.getContext().getAuthentication().getName()
        );
        
        // Get Facebook pages
        String pagesUrl = "https://graph.facebook.com/v18.0/me/accounts?access_token=" + 
                        client.getAccessToken().getTokenValue();
        
        // Save connected pages to database
        // ...
    }
}
```

### Instagram Integration

Instagram Business accounts are accessed through Facebook Graph API:

```java
@Service
public class InstagramOAuthService {
    
    public SocialAccount handleCallback(String code, String state) {
        // Similar to Facebook, get OAuth token
        OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(
            "instagram", 
            SecurityContextHolder.getContext().getAuthentication().getName()
        );
        
        // Get Instagram Business accounts
        String url = "https://graph.facebook.com/v18.0/me/accounts?" +
                    "fields=instagram_business_account{id,username,profile_picture_url}" +
                    "&access_token=" + client.getAccessToken().getTokenValue();
        
        // Parse response and save Instagram accounts
        // ...
    }
}
```

## Batch Posting Implementation

```java
@Service
public class SocialPostingService {
    
    @Autowired
    private TikTokPostingService tikTokPostingService;
    
    @Autowired
    private FacebookPostingService facebookPostingService;
    
    @Autowired
    private InstagramPostingService instagramPostingService;
    
    public BatchPostResult batchPost(Long userId, BatchPostRequest request) {
        BatchPostResult result = new BatchPostResult();
        List<PostResult> postResults = new ArrayList<>();
        
        for (Long accountId : request.getSelectedAccountIds()) {
            SocialAccount account = socialAccountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));
            
            if (!account.getUserId().equals(userId)) {
                throw new RuntimeException("Unauthorized access to account");
            }
            
            try {
                PostResult postResult;
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
                postResults.add(new PostResult(accountId, false, e.getMessage()));
            }
        }
        
        result.setResults(postResults);
        return result;
    }
}
```

## Environment Variables

Create a `.env` file or configure in your deployment environment:

```bash
# Facebook
FACEBOOK_CLIENT_ID=your_facebook_app_id
FACEBOOK_CLIENT_SECRET=your_facebook_app_secret

# Instagram
INSTAGRAM_CLIENT_ID=your_instagram_app_id
INSTAGRAM_CLIENT_SECRET=your_instagram_app_secret

# TikTok
TIKTOK_CLIENT_ID=your_tiktok_client_key
TIKTOK_CLIENT_SECRET=your_tiktok_client_secret
TIKTOK_REDIRECT_URI=http://localhost:8080/api/oauth/callback/tiktok
```

## Getting Platform Credentials

### Facebook/Instagram
1. Go to [Facebook Developers](https://developers.facebook.com/)
2. Create a new app (Select "Business" type)
3. Add "Facebook Login" product
4. Configure OAuth redirect URIs
5. Get App ID and App Secret
6. For Instagram: Add "Instagram Basic Display" product

### TikTok
1. Go to [TikTok for Developers](https://developers.tiktok.com/)
2. Create a new app
3. Configure OAuth redirect URI
4. Get Client Key and Client Secret

## Security Considerations

1. **Token Storage**: Encrypt access tokens in the database
2. **Token Refresh**: Implement automatic token refresh before expiration
3. **Rate Limiting**: Respect platform API rate limits
4. **User Authorization**: Ensure users can only access their own accounts
5. **HTTPS**: Always use HTTPS for OAuth callbacks

## Testing

Use the following curl commands to test endpoints:

```bash
# Get OAuth URL
curl "http://localhost:8080/api/oauth/connect/tiktok?redirectUri=http://localhost:3000/callback"

# Get user accounts
curl -H "Authorization: Bearer YOUR_JWT_TOKEN" \
     http://localhost:8080/api/oauth/accounts

# Batch post
curl -X POST \
     -H "Authorization: Bearer YOUR_JWT_TOKEN" \
     -H "Content-Type: application/json" \
     -d '{
       "campaignId": 1,
       "content": "Test post",
       "mediaUrl": "https://example.com/image.jpg",
       "selectedAccountIds": [1, 2, 3]
     }' \
     http://localhost:8080/api/social/batch-post
```

## Frontend Integration

Update the JavaScript in `social.html` to call these endpoints:

```javascript
async function connectOAuth(platform) {
    try {
        const response = await fetch(`/api/oauth/connect/${platform}?redirectUri=${window.location.origin}/callback`);
        const authUrl = await response.text();
        window.location.href = authUrl;
    } catch (error) {
        console.error('OAuth connection failed:', error);
        alert('Không thể kết nối tài khoản. Vui lòng thử lại.');
    }
}

async function removeAccount(accountId) {
    if (confirm('Bạn có chắc muốn ngắt kết nối tài khoản này?')) {
        try {
            await fetch(`/api/oauth/accounts/${accountId}`, {
                method: 'DELETE',
                headers: {
                    'Authorization': `Bearer ${getAuthToken()}`
                }
            });
            // Refresh account list
            loadAccounts();
        } catch (error) {
            console.error('Failed to remove account:', error);
        }
    }
}

async function batchPost() {
    const selectedAccounts = getSelectedAccounts();
    if (selectedAccounts.length === 0) {
        alert('Vui lòng chọn ít nhất một tài khoản để đăng bài.');
        return;
    }

    try {
        const response = await fetch('/api/social/batch-post', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${getAuthToken()}`
            },
            body: JSON.stringify({
                campaignId: campaignId,
                content: content,
                mediaUrl: mediaUrl,
                selectedAccountIds: selectedAccounts
            })
        });
        const result = await response.json();
        alert('Đăng bài thành công!');
    } catch (error) {
        console.error('Batch post failed:', error);
        alert('Đăng bài thất bại. Vui lòng thử lại.');
    }
}
```

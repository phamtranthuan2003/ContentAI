package com.suoidesign.repository;

import com.suoidesign.entity.SocialAccount;
import com.suoidesign.entity.SocialPlatform;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SocialAccountRepository extends JpaRepository<SocialAccount, Long> {
    
    List<SocialAccount> findByUserId(Long userId);
    
    List<SocialAccount> findByUserIdAndPlatform(Long userId, SocialPlatform platform);
    
    List<SocialAccount> findByUserIdAndIsActiveTrue(Long userId);
    
    Optional<SocialAccount> findByUserIdAndPlatformAccountId(Long userId, String platformAccountId);
    
    void deleteByUserIdAndId(Long userId, Long id);
}

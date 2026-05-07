package com.suoidesign.repository;

import com.suoidesign.entity.Campaign;
import com.suoidesign.entity.CampaignStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CampaignRepository extends JpaRepository<Campaign, Long> {
    
    List<Campaign> findByUserId(Long userId);
    
    List<Campaign> findByUserIdAndStatus(Long userId, CampaignStatus status);
    
    List<Campaign> findByUserIdOrderByCreatedAtDesc(Long userId);
}

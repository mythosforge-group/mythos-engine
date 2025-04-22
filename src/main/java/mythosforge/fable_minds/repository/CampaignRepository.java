package mythosforge.fable_minds.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import mythosforge.fable_minds.models.Campaign;

public interface CampaignRepository extends JpaRepository<Campaign, Long> {
    List<Campaign> findByOwnerId(Long ownerId);
}

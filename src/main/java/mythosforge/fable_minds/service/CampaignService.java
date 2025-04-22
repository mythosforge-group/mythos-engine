package mythosforge.fable_minds.service;

import java.util.List;

import org.springframework.stereotype.Service;

import mythosforge.fable_minds.models.Campaign;
import mythosforge.fable_minds.repository.CampaignRepository;

@Service
public class CampaignService {

    private final CampaignRepository campaignRepository;

    public CampaignService(CampaignRepository campaignRepository) {
        this.campaignRepository = campaignRepository;
    }

    public Campaign createCampaign(Campaign campaign) {
        return campaignRepository.save(campaign);
    }

    public List<Campaign> getCampaignsByUserId(Long userId) {
        return campaignRepository.findByOwnerId(userId);
    }
}

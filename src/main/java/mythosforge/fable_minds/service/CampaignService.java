package mythosforge.fable_minds.service;

import java.util.List;

import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import mythosforge.fable_minds.models.Campaign;
import mythosforge.fable_minds.repository.CampaignRepository;
import mythosforge.fable_minds.service.interfaces.ICampaignService;

@Service
public class CampaignService implements ICampaignService {

    private final CampaignRepository campaignRepository;

    public CampaignService(CampaignRepository campaignRepository) {
        this.campaignRepository = campaignRepository;
    }

    @Override
    public Campaign create(Campaign campaign) {
        return campaignRepository.save(campaign);
    }

    @Override
    public Campaign update(Long id, Campaign campaign) {
        Campaign existing = campaignRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Campaign not found: " + id));
        existing.setTitle(campaign.getTitle());
        existing.setDescription(campaign.getDescription());
        existing.setSystem(campaign.getSystem());
        existing.setUser(campaign.getUser());
        return campaignRepository.save(existing);
    }

    @Override
    public Campaign findById(Long id) {
        return campaignRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Campaign not found: " + id));
    }

    @Override
    public List<Campaign> findAll() {
        return campaignRepository.findAll();
    }

    @Override
    public List<Campaign> findByUserId(Long userId) {
        return campaignRepository.findByOwnerId(userId);
    }

    @Override
    public void delete(Long id) {
        if (!campaignRepository.existsById(id)) {
            throw new EntityNotFoundException("Campaign not found: " + id);
        }
        campaignRepository.deleteById(id);
    }
}

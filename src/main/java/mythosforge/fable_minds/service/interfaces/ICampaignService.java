// src/main/java/mythosforge/fable_minds/service/CampaignService.java
package mythosforge.fable_minds.service.interfaces;

import java.util.List;
import mythosforge.fable_minds.models.Campaign;

public interface ICampaignService {
    Campaign create(Campaign campaign);
    Campaign update(Long id, Campaign campaign);
    Campaign findById(Long id);
    List<Campaign> findAll();
    List<Campaign> findByUserId(Long userId);
    void delete(Long id);
}

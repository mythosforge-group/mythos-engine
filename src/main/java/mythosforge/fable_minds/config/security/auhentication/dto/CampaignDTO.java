package mythosforge.fable_minds.config.security.auhentication.dto;

import mythosforge.fable_minds.models.Campaign;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CampaignDTO {
    private Long id;
    private String title;
    private String description;
    private String systemName;

    public CampaignDTO(Campaign campaign) {
        this.id = campaign.getId();
        this.title = campaign.getTitle();
        this.description = campaign.getDescription();
        this.systemName = campaign.getSystem().getName();
    }
}

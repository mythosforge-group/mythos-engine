package mythosengine.core.modules.content;

import mythosengine.core.template.RpgTemplate;
import mythosengine.services.llm.LlmClientServiceLore;
import mythosengine.core.template.GenericTemplateService; // <<< MUDANÇA

import mythosforge.fable_minds.llm.ResponseParser;
import mythosforge.fable_minds.models.Campaign;
import mythosforge.fable_minds.models.CharacterClass;
import mythosforge.fable_minds.models.Race;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class SystemAdaptiveGeneratorModule implements IContentGeneratorModule {

    private final LlmClientServiceLore llmClient;
    private final GenericTemplateService templateService; // <<< MUDANÇA

    public SystemAdaptiveGeneratorModule(LlmClientServiceLore llmClient, GenericTemplateService templateService) { // <<< MUDANÇA
        this.llmClient = llmClient;
        this.templateService = templateService;
    }

    @Override
    public boolean supports(ContentGenerationContext context) {
        Campaign campaign = (Campaign) context.getParameters().get("campaign");
        if (campaign == null || campaign.getSystem() == null) {
            return false;
        }

        String templateName = campaign.getSystem().getName().toLowerCase().replace(" ", "_") + "/" + context.getGenerationType().toLowerCase();
        return templateService.templateExists("rpg_systems", templateName); // <<< MUDANÇA
    }

    @Override
    public GeneratedContent generate(ContentGenerationContext context) {
        Map<String, Object> params = context.getParameters();
        Campaign campaign = (Campaign) params.get("campaign");
        Race race = (Race) params.get("race");
        CharacterClass characterClass = (CharacterClass) params.get("class");

        String templateName = campaign.getSystem().getName().toLowerCase().replace(" ", "_") + "/" + context.getGenerationType().toLowerCase();
        RpgTemplate template = templateService.getTemplate("rpg_systems", templateName); // <<< MUDANÇA

        Map<String, Object> templateData = new HashMap<>();
        templateData.put("system.name", campaign.getSystem().getName());
        templateData.put("campaign.title", campaign.getTitle());
        templateData.put("campaign.description", campaign.getDescription());
        templateData.put("race.name", race.getName());
        templateData.put("characterClass.name", characterClass.getName());

        String finalPrompt = templateService.processTemplate(template, templateData);

        String llmResponse = llmClient.request(finalPrompt);

        String historia = ResponseParser.extrairHistoriaLimpa(llmResponse);
        String nome = ResponseParser.extrairNome(llmResponse);

        return GeneratedContent.builder()
                .mainText(historia)
                .metadata(Map.of("nome", nome))
                .build();
    }

    @Override
    public String getModuleName() {
        return "";
    }

    @Override
    public String getVersion() {
        return "";
    }


}

package mythosengine.core.modules.content;

import mythosengine.core.template.RpgTemplate;
import mythosengine.core.template.RpgTemplateService;
import mythosforge.fable_minds.llm.LlmClientService;
import mythosforge.fable_minds.llm.ResponseParser;
import mythosforge.fable_minds.models.Campaign;
import mythosforge.fable_minds.models.CharacterClass;
import mythosforge.fable_minds.models.Race;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Primary
public class SystemAdaptiveGeneratorModule implements IContentGeneratorModule {

    private final LlmClientService llmClient;
    private final RpgTemplateService templateService;

    public SystemAdaptiveGeneratorModule(LlmClientService llmClient, RpgTemplateService templateService) {
        this.llmClient = llmClient;
        this.templateService = templateService;
    }

    @Override
    public boolean supports(ContentGenerationContext context) {

        Campaign campaign = (Campaign) context.getParameters().get("campaign");
        if (campaign == null || campaign.getSystem() == null) {
            return false;
        }

        return templateService.templateExists(
                campaign.getSystem().getName(),
                context.getGenerationType()
        );
    }

    @Override
    public GeneratedContent generate(ContentGenerationContext context) {
        // 1. Extrair parâmetros do contexto
        Map<String, Object> params = context.getParameters();
        Campaign campaign = (Campaign) params.get("campaign");
        Race race = (Race) params.get("race");
        CharacterClass characterClass = (CharacterClass) params.get("class");

        // 2. Carregar o template apropriado
        RpgTemplate template = templateService.getTemplate(
            campaign.getSystem().getName(),
            context.getGenerationType()
        );

        // 3. Montar o mapa de dados para preencher o template
        Map<String, Object> templateData = new HashMap<>();
        templateData.put("systemName", campaign.getSystem().getName());
        templateData.put("campaignTitle", campaign.getTitle());
        templateData.put("campaignDescription", campaign.getDescription());
        templateData.put("raceName", race.getName());
        templateData.put("className", characterClass.getName());

        // 4. Processar o template para gerar o prompt final
        String finalPrompt = templateService.processTemplate(template, templateData);
        
        // 5. Chamar a LLM com o prompt adaptado
        String llmResponse = llmClient.request(finalPrompt);

        // 6. Extrair dados da resposta e construir o resultado
        String historia = ResponseParser.extrairHistoriaLimpa(llmResponse);
        String nome = ResponseParser.extrairNome(llmResponse);

        return GeneratedContent.builder()
                .mainText(historia)
                .metadata(Map.of("nome", nome))
                .build();
    }

    @Override
    public String getModuleName() {
        return "System Adaptive Content Generator";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }
}
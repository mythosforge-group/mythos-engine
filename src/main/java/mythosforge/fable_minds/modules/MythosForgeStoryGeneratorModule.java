package mythosforge.fable_minds.modules;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import mythosengine.core.modules.content.IContentGeneratorModule;
import mythosengine.core.modules.content.ContentGenerationContext;
import mythosengine.core.modules.content.GeneratedContent;
import mythosforge.fable_minds.dto.SystemDTO;
import mythosforge.fable_minds.llm.LlmClientService;
import mythosforge.fable_minds.llm.PromptBuilder;
import mythosforge.fable_minds.llm.ResponseParser;
import mythosforge.fable_minds.models.Campaign;
import mythosforge.fable_minds.models.CharacterClass;
import mythosforge.fable_minds.models.Race;

@Service // Marcamos como um serviço Spring para ser descoberto automaticamente
public class MythosForgeStoryGeneratorModule implements IContentGeneratorModule {

    private final LlmClientService llmClient;

    public MythosForgeStoryGeneratorModule(LlmClientService llmClient) {
        this.llmClient = llmClient;
    }

    @Override
    public boolean supports(ContentGenerationContext context) {
        // Este módulo específico sabe gerar histórias e missões para o Mythos Forge
        List<String> supportedTypes = List.of("HISTORIA_PERSONAGEM", "MISSAO_SECUNDARIA");
        return supportedTypes.contains(context.getGenerationType());
    }

    @Override
    public GeneratedContent generate(ContentGenerationContext context) {
        if (!supports(context)) {
            return null;
        }

        if ("HISTORIA_PERSONAGEM".equals(context.getGenerationType())) {
            // 1. Extrair parâmetros do contexto
            Map<String, Object> params = context.getParameters();
            Campaign campaign = (Campaign) params.get("campaign");
            Race race = (Race) params.get("race");
            CharacterClass characterClass = (CharacterClass) params.get("class");

            // 2. Usar o PromptBuilder para montar o prompt
            var campaignSystem = campaign.getSystem();
            if (campaignSystem == null) {
                throw new IllegalArgumentException("O sistema da campanha não pode ser nulo.");
            }
            SystemDTO system = new SystemDTO(
                campaignSystem.getId(),
                campaignSystem.getName(),
                campaignSystem.getDescription()
            );
            String prompt = PromptBuilder.buildNamedPrompt(
                system,
                campaign,
                race.getName(),
                characterClass.getName()
            );

            // 3. Chamar a LLM
            String llmResponse = llmClient.request(prompt);

            // 4. Usar o ResponseParser para limpar e extrair dados
            String historia = ResponseParser.extrairHistoriaLimpa(llmResponse);
            String nome = ResponseParser.extrairNome(llmResponse);

            // 5. Construir e retornar o objeto de resultado com metadados
            return GeneratedContent.builder()
                    .mainText(historia)
                    .metadata(Map.of("nome", nome)) // Adicionamos o nome extraído aqui
                    .build();
        }

        return null;
    }

    @Override
    public String getModuleName() { //TODO: retornar o nome do módulo que está no pom.xml e não uma fixa
        return "MythosForge Story Generator"; 
    }

    @Override
    public String getVersion() { //TODO: retornar a versão que está no pom.xml e não uma fixa
        return "1.0.0";
    }
}



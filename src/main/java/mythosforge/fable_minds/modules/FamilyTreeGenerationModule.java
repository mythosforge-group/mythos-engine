package mythosforge.fable_minds.modules;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import mythosengine.core.entity.Entity;
import mythosengine.core.persistence.PersistencePort;
import mythosengine.spi.content.ContentGenerationContext;
import mythosengine.spi.content.GeneratedContent;
import mythosengine.spi.content.IContentGeneratorModule;
import mythosengine.spi.prompt.PromptBuilder;
import mythosforge.fable_minds.llm.LlmClientService;
import mythosforge.fable_minds.llm.ResponseParser;
import mythosforge.fable_minds.models.CharacterDnd;
import mythosforge.fable_minds.models.Pessoa;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class FamilyTreeGenerationModule implements IContentGeneratorModule {

    private final LlmClientService llmClient;
    private final PersistencePort persistencePort;
    private final ObjectMapper objectMapper;
    private final List<PromptBuilder> promptBuilders;

    public FamilyTreeGenerationModule(LlmClientService llmClient, PersistencePort persistencePort, List<PromptBuilder> promptBuilders) {
        this.llmClient = llmClient;
        this.persistencePort = persistencePort;
        this.promptBuilders = promptBuilders;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public boolean supports(ContentGenerationContext context) {
        return "GERAR_LINHAGEM".equals(context.getGenerationType());
    }

    @Override
    public GeneratedContent generate(ContentGenerationContext context) {
        CharacterDnd personagemPrincipalApp = (CharacterDnd) context.getParameters().get("character");
        UUID entityId = personagemPrincipalApp.getEntityId();
        Entity personagemPrincipalFrameworkEntity = persistencePort.findById(entityId)
                .orElseThrow(() -> new IllegalStateException("Entidade do framework não encontrada."));

        PromptBuilder builder = promptBuilders.stream()
                .filter(b -> b.supports(context))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Nenhum PromptBuilder para 'GERAR_LINHAGEM' encontrado."));

        String prompt = builder.build(context);
        String llmResponse = llmClient.request(prompt);
        String jsonResponse = ResponseParser.extrairHistoriaLimpa(llmResponse)
                .replaceAll("(?s)^```json\\s*", "").replaceAll("(?s)```\\s*$", "");

        try {
            Pessoa rootPessoa = objectMapper.readValue(jsonResponse, Pessoa.class);
            List<Entity> createdEntities = new ArrayList<>();
            createEntitiesFromPessoa(rootPessoa, personagemPrincipalFrameworkEntity, createdEntities);

            return GeneratedContent.builder()
                    .mainText("Linhagem gerada e entidades criadas com sucesso.")
                    .createdEntities(createdEntities)
                    .build();
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Falha ao processar JSON da LLM para a árvore genealógica", e);
        }
    }
    
    // ... método createEntitiesFromPessoa e outros métodos auxiliares permanecem os mesmos ...
    private void createEntitiesFromPessoa(Pessoa pessoa, Entity childEntity, List<Entity> allCreatedEntities) {
        if (pessoa.getPais() == null || pessoa.getPais().isEmpty()) return;
        for (Pessoa paiPessoa : pessoa.getPais()) {
            Entity paiEntity = new Entity("Personagem_Linhagem");
            paiEntity.addProperty("nome", paiPessoa.getNome());
            paiEntity.addProperty("ocupacao", paiPessoa.getOcupacao());
            paiEntity.addProperty("origem", paiPessoa.getOrigem());
            paiEntity.addRelationship(childEntity.getId(), "É_PAI_DE");
            persistencePort.save(paiEntity);
            allCreatedEntities.add(paiEntity);
            createEntitiesFromPessoa(paiPessoa, paiEntity, allCreatedEntities);
        }
    }

    @Override
    public String getModuleName() { return "Family Tree Generator"; }
    @Override
    public String getVersion() { return "3.0.0"; }
}
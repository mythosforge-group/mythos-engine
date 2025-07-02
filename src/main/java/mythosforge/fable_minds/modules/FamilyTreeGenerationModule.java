package mythosforge.fable_minds.modules;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import mythosengine.core.entity.Entity;
import mythosengine.core.modules.content.ContentGenerationContext;
import mythosengine.core.modules.content.GeneratedContent;
import mythosengine.core.modules.content.IContentGeneratorModule;
import mythosengine.core.persistence.PersistencePort;
import mythosforge.fable_minds.llm.LlmClientService;
import mythosforge.fable_minds.llm.PromptBuilder;
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

    public FamilyTreeGenerationModule(LlmClientService llmClient, PersistencePort persistencePort) {
        this.llmClient = llmClient;
        this.persistencePort = persistencePort;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public boolean supports(ContentGenerationContext context) {
        return "GERAR_LINHAGEM".equals(context.getGenerationType());
    }

    @Override
    public GeneratedContent generate(ContentGenerationContext context) {

        CharacterDnd personagemPrincipalApp = (CharacterDnd) context.getParameters().get("character");



        // 2. Usar o ID da "ponte" para carregar a ENTIDADE do FRAMEWORK correspondente
        UUID entityId = personagemPrincipalApp.getEntityId();


        if (entityId == null) {
            throw new IllegalStateException("O CharacterDnd com ID " + personagemPrincipalApp.getId() + " não possui um entityId do framework vinculado.");
        }

        Entity personagemPrincipalFrameworkEntity = persistencePort.findById(entityId)
                .orElseThrow(() -> new IllegalStateException(
                        "FATAL: A entidade do framework (" + entityId + ") para o personagem '" + personagemPrincipalApp.getNome() + "' não foi encontrada."
                ));

        // --- FIM DA CORREÇÃO ---



        String prompt = PromptBuilder.buildFamilyTreePrompt(
                personagemPrincipalApp.getNome(),
                personagemPrincipalApp.getHistoria(),
                personagemPrincipalApp.getRaca().getName(),
                personagemPrincipalApp.getCharacterClass().getName()
        );
        String llmResponse = llmClient.request(prompt);
        String jsonResponse = ResponseParser.extrairHistoriaLimpa(llmResponse)
                .replaceAll("(?s)^```json\\s*", "")
                .replaceAll("(?s)```\\s*$", "");

        try {
            // 4. Deserializar o JSON para o DTO temporário 'Pessoa'
            Pessoa rootPessoa = objectMapper.readValue(jsonResponse, Pessoa.class);

            // 5. Converter a estrutura 'Pessoa' em Entidades e Relações do framework
            List<Entity> createdEntities = new ArrayList<>();

            // AGORA a chamada está correta: passamos a ENTIDADE que carregamos do framework
            createEntitiesFromPessoa(rootPessoa, personagemPrincipalFrameworkEntity, createdEntities);

            return GeneratedContent.builder()
                    .mainText("Linhagem gerada e entidades criadas com sucesso.")
                    .createdEntities(createdEntities)
                    .build();

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Falha ao processar JSON da LLM para a árvore genealógica", e);
        }
    }

    /**
     * Método recursivo que converte a árvore de Pessoas em Entidades e Relações.
     */
    private void createEntitiesFromPessoa(Pessoa pessoa, Entity childEntity, List<Entity> allCreatedEntities) {
        if (pessoa.getPais() == null || pessoa.getPais().isEmpty()) {
            return;
        }

        for (Pessoa paiPessoa : pessoa.getPais()) {

            Entity paiEntity = new Entity("Personagem_Linhagem");
            paiEntity.addProperty("nome", paiPessoa.getNome());
            paiEntity.addProperty("ocupacao", paiPessoa.getOcupacao());
            paiEntity.addProperty("origem", paiPessoa.getOrigem());

            // Cria a relação: pai -> filho
            paiEntity.addRelationship(childEntity.getId(), "É_PAI_DE");

            // Salva a nova entidade e a adiciona à lista
            persistencePort.save(paiEntity);
            allCreatedEntities.add(paiEntity);

            // Continua a recursão para os avós
            createEntitiesFromPessoa(paiPessoa, paiEntity, allCreatedEntities);
        }
    }

    // Métodos getModuleName() e getVersion()
    @Override
    public String getModuleName() { return "Family Tree Generator"; }
    @Override
    public String getVersion() { return "1.0.0"; }
}

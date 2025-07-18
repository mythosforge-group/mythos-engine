package mythosforge.fable_minds.service;

import mythosengine.core.engine.ContentGenerationEngine;
import mythosengine.spi.content.ContentGenerationContext;
import mythosengine.spi.content.GeneratedContent;
import mythosforge.fable_minds.llm.ResponseParser;
import mythosforge.fable_minds.models.*;
import mythosforge.fable_minds.service.interfaces.ICharacterGenerationService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class CharacterGenerationService implements ICharacterGenerationService {

    private final ContentGenerationEngine generationEngine;
    private final CampaignService campaignService;
    private final RaceService raceService;
    private final CharacterClassService classService;
    private final CharacterDndService characterDndService;

    public CharacterGenerationService(
            ContentGenerationEngine generationEngine,
            CampaignService campaignService,
            RaceService raceService,
            CharacterClassService classService,
            CharacterDndService characterDndService
    ) {
        this.generationEngine = generationEngine;
        this.campaignService = campaignService;
        this.raceService = raceService;
        this.classService = classService;
        this.characterDndService = characterDndService;
    }

    private ContentGenerationContext buildCharacterContext(Long campaignId, Long raceId, Long classId) {
        Campaign campaign = campaignService.findById(campaignId);
        Race race = raceService.findById(raceId);
        CharacterClass characterClass = classService.findById(classId);

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("campaign", campaign);
        parameters.put("race", race);
        parameters.put("class", characterClass);

        return ContentGenerationContext.builder()
                .generationType("HISTORIA_PERSONAGEM")
                .parameters(parameters)
                .build();
    }

    @Override
    public String gerarFicha(Long campaignId, Long raceId, Long classId) {
        ContentGenerationContext context = buildCharacterContext(campaignId, raceId, classId);
        GeneratedContent result = generationEngine.process(context);
        // Retorna o nome e a história combinados
        return result.getMetadata().get("nome") + "\n\n" + result.getMainText();
    }

    @Override
    public CharacterDnd gerarFichaESalvar(Long campaignId, Long raceId, Long classId) {
        ContentGenerationContext context = buildCharacterContext(campaignId, raceId, classId);
        GeneratedContent result = generationEngine.process(context);

        CharacterDnd novoPersonagem = new CharacterDnd();
        novoPersonagem.setNome((String) result.getMetadata().get("nome"));
        novoPersonagem.setHistoria(result.getMainText());
        novoPersonagem.setNivel(1);
        novoPersonagem.setXp(0);
        novoPersonagem.setCampanha((Campaign) context.getParameters().get("campaign"));
        novoPersonagem.setRaca((Race) context.getParameters().get("race"));
        novoPersonagem.setCharacterClass((CharacterClass) context.getParameters().get("class"));
        novoPersonagem.setForca(rolarAtributo());
        novoPersonagem.setDestreza(rolarAtributo());
        novoPersonagem.setConstituicao(rolarAtributo());
        novoPersonagem.setInteligencia(rolarAtributo());
        novoPersonagem.setSabedoria(rolarAtributo());
        novoPersonagem.setCarisma(rolarAtributo());

        // O CharacterDndService já lida com a criação da entidade do framework
        return characterDndService.create(novoPersonagem);
    }

    @Override
    public String gerarLinhagem(Long characterId) {
        CharacterDnd personagem = characterDndService.findById(characterId);
        ContentGenerationContext context = ContentGenerationContext.builder()
                .generationType("GERAR_LINHAGEM")
                .parameters(Map.of("character", personagem))
                .build();
        
        GeneratedContent result = generationEngine.process(context);
        return result.getMainText(); // Retorna a mensagem de sucesso do módulo
    }

    private int rolarAtributo() {
        return 8 + (int)(Math.random() * 11);
    }
}
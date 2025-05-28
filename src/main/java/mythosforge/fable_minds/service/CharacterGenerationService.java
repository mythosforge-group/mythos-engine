package mythosforge.fable_minds.service;

import mythosforge.fable_minds.config.security.auhentication.dto.CharacterClassDTO;
import mythosforge.fable_minds.config.security.auhentication.dto.RaceDTO;
import mythosforge.fable_minds.config.security.auhentication.dto.SystemDTO;
import mythosforge.fable_minds.llm.PromptBuilder;
import mythosforge.fable_minds.llm.ResponseParser;
import mythosforge.fable_minds.llm.LlmClientService;
import mythosforge.fable_minds.models.*;
import mythosforge.fable_minds.service.interfaces.ICharacterGenerationService;

import org.springframework.stereotype.Service;

@Service
public class CharacterGenerationService implements ICharacterGenerationService {

    private final CampaignService campaignService;
    private final RaceService raceService;
    private final CharacterClassService classService;
    private final SystemService systemService;
    private final LlmClientService llmClient;
    private final CharacterDndService characterDndService;

    public CharacterGenerationService(
            CampaignService campaignService,
            RaceService raceService,
            CharacterClassService classService,
            SystemService systemService,
            LlmClientService llmClient,
            CharacterDndService characterDndService
    ) {
        this.characterDndService = characterDndService;
        this.campaignService = campaignService;
        this.raceService = raceService;
        this.classService = classService;
        this.systemService = systemService;
        this.llmClient = llmClient;
    }

    @Override
    public String gerarFicha(Long campaignId, Long raceId, Long classId) {
        Campaign campanha = campaignService.findById(campaignId);
        RaceDTO raca = raceService.findByIdDto(raceId);
        CharacterClassDTO clazz = classService.findByIdDto(classId);
        SystemDTO sistema = systemService.findByIdDto(campanha.getSystem().getId());

        String prompt = PromptBuilder.buildBasicPrompt(
                sistema, campanha, raca.getName(), clazz.getName()
        );
        return llmClient.request(prompt);
    }

    @Override
    public CharacterDnd gerarFichaESalvar(Long campaignId, Long raceId, Long classId) {
        Campaign campanha = campaignService.findById(campaignId);
        Race racaModel = raceService.findById(raceId);
        CharacterClass characterClass = classService.findById(classId);
        SystemDTO sistema = systemService.findByIdDto(campanha.getSystem().getId());

        String prompt  = PromptBuilder.buildNamedPrompt(
                sistema,
                campanha,
                racaModel.getName(),
                characterClass.getName()
        );
        String conteudo = llmClient.request(prompt);

        String nomeExtraido = ResponseParser.extrairNome(conteudo);
        String historiaLimpa = ResponseParser.extrairHistoriaLimpa(conteudo);

        CharacterDnd novoPersonagem = new CharacterDnd();
        novoPersonagem.setNome(nomeExtraido);
        novoPersonagem.setHistoria(historiaLimpa);
        novoPersonagem.setNivel(1);
        novoPersonagem.setXp(0);
        novoPersonagem.setCampanha(campanha);
        novoPersonagem.setRaca(racaModel);
        novoPersonagem.setCharacterClass(characterClass);
        novoPersonagem.setForca(rolarAtributo());
        novoPersonagem.setDestreza(rolarAtributo());
        novoPersonagem.setConstituicao(rolarAtributo());
        novoPersonagem.setInteligencia(rolarAtributo());
        novoPersonagem.setSabedoria(rolarAtributo());
        novoPersonagem.setCarisma(rolarAtributo());

        return novoPersonagem;
    }

    @Override
    public String gerarLinhagem(Long characterId) {
        CharacterDnd personagem = characterDndService.findById(characterId);

        String prompt = PromptBuilder.buildFamilyTreePrompt(
                personagem.getNome(),
                personagem.getHistoria(),
                personagem.getRaca().getName(),
                personagem.getCharacterClass().getName()
        );
        String conteudo = llmClient.request(prompt);

        String respostaJson = ResponseParser.extrairHistoriaLimpa(conteudo);

        return respostaJson;
    }

    private int rolarAtributo() {
        return 8 + (int)(Math.random() * 11);
    }
}

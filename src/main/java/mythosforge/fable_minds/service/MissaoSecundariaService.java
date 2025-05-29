package mythosforge.fable_minds.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentConfig;

import jakarta.persistence.EntityNotFoundException;
import mythosforge.fable_minds.models.Campaign;
import mythosforge.fable_minds.models.MissaoSecundaria;
import mythosforge.fable_minds.models.Character;
import mythosforge.fable_minds.repository.CampaignRepository;
import mythosforge.fable_minds.repository.CharacterRepository;
import mythosforge.fable_minds.repository.MissaoSecundariaRepository;
import mythosforge.fable_minds.repository.UserRepository;

@Service
public class MissaoSecundariaService {
    private final MissaoSecundariaRepository missaoSecundariaRepository;
    private final UserRepository   userRepository;
    private final CampaignRepository campaignRepository;
    private final CharacterRepository characterRepository;

    public MissaoSecundariaService(
        MissaoSecundariaRepository missaoSecundariaRepository,
        UserRepository userRepository,
        CampaignRepository campaignRepository,
        CharacterRepository characterRepository
    ) {
        this.missaoSecundariaRepository = missaoSecundariaRepository;
        this.userRepository = userRepository;
        this.campaignRepository = campaignRepository;
        this.characterRepository = characterRepository;
    }

    /**
     * Gera uma missão secundária aleatória um personagem aleatório de uma campanha aleatória.
     * Se o usuário ainda não tiver campanhas e/ou as campanhas não tiverem personagens, lança uma exceção.
     * @param username O nome de usuário do usuário que fez a requisição.
     * @return Uma MissaoSecundaria gerada aleatoriamente.
    */
    public MissaoSecundaria gerarMissaoSecundariaAleatoria(String username) {
        var user = this.userRepository.findByUsername(username)
            .orElseThrow(() -> new EntityNotFoundException(username + " não encontrado."));
        var campaigns = this.campaignRepository.findByUserId(user.getId());
        if (campaigns.isEmpty()) {
            throw new IllegalArgumentException("Usuário não possui campanhas.");
        }
        var campaignsWithCharacters = campaigns.stream()
            .filter(campaign -> !this.characterRepository.findByCampanhaId(campaign.getId()).isEmpty())
            .toList();
        if (campaignsWithCharacters.isEmpty()) {
            throw new EntityNotFoundException("Nenhuma campanha possui personagens cadastrados.");
        }
        var randomCampaign = campaignsWithCharacters.get((int) (Math.random() * campaignsWithCharacters.size()));
        
        var characters = this.characterRepository.findByCampanhaId(randomCampaign.getId());
        var randomCharacter = characters.get((int) (Math.random() * characters.size()));
        var missaoSecundaria = gerarMissaoSecundariaComGemini(randomCharacter, randomCampaign);
        return this.missaoSecundariaRepository.save(missaoSecundaria);
    }

    /**
     * Gera uma missão secundária para um personagem específico de uma campanha específica.
     * @param characterId O ID do personagem escolhido para a missão secundária.
     * @param campaignId O ID da campanha escolhida para a missão secundária.
     * @param username O nome de usuário do usuário que fez a requisição.
     * @return Uma MissaoSecundaria gerada com o Gemini AI.
    */
    public MissaoSecundaria gerarMissaoSecundaria(Long characterId, String username) {
        var user = this.userRepository.findByUsername(username).get();
        var character = this.characterRepository.findById(characterId)
            .orElseThrow(() -> new EntityNotFoundException("Personagem com ID " + characterId + " não encontrado."));
        var campaign = character.getCampanha();
        if (campaign == null || !campaign.getUser().getId().equals(user.getId())) {
            throw new EntityNotFoundException("Campanha não encontrada ou não pertence ao usuário.");
        }
        var missaoSecundaria = gerarMissaoSecundariaComGemini(character, campaign);
        return this.missaoSecundariaRepository.save(missaoSecundaria);
    }

    /**
     * Gera um titulo e uma história para a missão secundária usando o Gemini AI.
     * O titulo tem um maxOutputTokens de 10 e a história tem um maxOutputTokens de 100.
     * @param character O personagem escolhido para a missão secundária.
     * @param campaign A campanha escolhida para a missão secundária.
     * return Uma MissaoSecundaria gerada com o Gemini AI.
    */
    private MissaoSecundaria gerarMissaoSecundariaComGemini(Character character, Campaign campaign) {
        var client = new Client();

        var titlePrompt = "Crie apenas um titulo curto para uma missão secundária baseada na seguinte história do personagem: " + character.getHistoria();
        var titleConfig = GenerateContentConfig.builder()
            .maxOutputTokens(10)
            .temperature(0.8F) // opcional. Vai ficar muito louco!!!
            .build();

        var titleResponse = client.models.generateContent(
            "gemini-2.0-flash",
            titlePrompt,
            titleConfig 
        );
        var title = titleResponse.text();

        var storyPrompt = "Crie uma história bem curta para uma missão secundária baseada na seguinte descrição da campanha: " + campaign.getDescription();
        var storyConfig = GenerateContentConfig.builder()
            .maxOutputTokens(500)
            .temperature(0.8F) // opcional. Vai ficar muito louco!!!
            .build();
        var storyResponse = client.models.generateContent(
                "gemini-2.0-flash",
                storyPrompt,
                storyConfig
        );
        var story = storyResponse.text();

        client.close();

        MissaoSecundaria missaoSecundaria = new MissaoSecundaria();
        missaoSecundaria.setTitulo(title);
        missaoSecundaria.setHistoria(story);
        missaoSecundaria.setCampanha(campaign); // Atribui a campanha aleatória escolhida
        missaoSecundaria.setPersonagens(List.of(character)); // Atribui o personagem aleatório escolhido
        return missaoSecundaria;
    }
}

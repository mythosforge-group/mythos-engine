package mythosforge.fable_minds.controller;

import jakarta.persistence.EntityNotFoundException;
import mythosforge.fable_minds.config.security.auhentication.dto.CharacterClassDTO;
import mythosforge.fable_minds.config.security.auhentication.dto.RaceDTO;
import mythosforge.fable_minds.config.security.auhentication.dto.SystemDTO;
import mythosforge.fable_minds.models.*;
import mythosforge.fable_minds.models.System;
import mythosforge.fable_minds.service.*;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/personagens/dnd")
public class CharacterGenerationController {

    private final CampaignService campaignService;
    private final RaceService raceService;
    private final CharacterClassService classService; // você deve ter um service para classes
    private final SystemService systemService;

    private final RestTemplate restTemplate = new RestTemplate();
    private final String url = "http://localhost:1234/v1/chat/completions";
    private final CharacterDndService characterDndService;

    public CharacterGenerationController(
            CampaignService campaignService,
            RaceService raceService,
            CharacterClassService classService,
            SystemService systemService,
            CharacterDndService characterDndService

    ) {
        this.campaignService = campaignService;
        this.raceService = raceService;
        this.classService = classService;
        this.systemService = systemService;
        this.characterDndService = characterDndService;
    }

    @PostMapping("/gerar")
    public ResponseEntity<String> gerarFicha(@RequestParam Long campaignId,
                                             @RequestParam Long raceId,
                                             @RequestParam Long classId) {


        Campaign campanha = campaignService.findById(campaignId);
        RaceDTO raca = raceService.findByIdDto(raceId);
        CharacterClassDTO clazz = classService.findByIdDto(classId); // Supondo que exista um model Class
        SystemDTO sistema = systemService.findByIdDto(campanha.getSystem().getId());


        String prompt = String.format("""
            Gere a história de um personagem para um RPG.
            O personagem pertence ao sistema: %s.
            Ele faz parte da campanha chamada "%s", que possui o seguinte enredo: "%s".
            O personagem é da raça: %s, e da classe: %s.
            Gere um background criativo, incluindo nome, motivações, traumas, relações e possíveis objetivos futuros.
            Apresente em um texto bem estruturado, como se fosse o histórico da ficha de personagem.
            """,
                sistema.getName(),
                campanha.getTitle(),
                campanha.getDescription(),
                raca.getName(),
                clazz.getName()
        );

        // Montar requisição para IA
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> payload = new HashMap<>();
        payload.put("model", "local-model");
        payload.put("messages", List.of(
                Map.of("role", "user", "content", prompt)
        ));

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

        Map<String, Object> choice = ((List<Map<String, Object>>) response.getBody().get("choices")).get(0);
        Map<String, String> message = (Map<String, String>) choice.get("message");

        return ResponseEntity.ok(message.get("content"));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handleNotFound(EntityNotFoundException ex) {
        return ResponseEntity.status(404).body(ex.getMessage());
    }

    @PostMapping("/gerar-e-salvar")
    public ResponseEntity<CharacterDnd> gerarFichaESalvar(@RequestParam Long campaignId,
                                                          @RequestParam Long raceId,
                                                          @RequestParam Long classId) {

        Campaign campanha = campaignService.findById(campaignId);
        Race raca = raceService.findById(raceId); // Aqui retorna um objeto do tipo correto
        CharacterClass characterClass = classService.findById(classId);
        SystemDTO sistema = systemService.findByIdDto(campanha.getSystem().getId());

        String prompt = String.format("""
        Gere a história de um personagem para um RPG.
        O personagem pertence ao sistema: %s.
        Ele faz parte da campanha chamada "%s", que possui o seguinte enredo: "%s".
        O personagem é da raça: %s, e da classe: %s.
        Gere um background criativo, incluindo nome, motivações, traumas, relações e possíveis objetivos futuros.
        Apresente em um texto bem estruturado, como se fosse o histórico da ficha de personagem.
        Comece com: Nome: <nome do personagem>
        """,
                sistema.getName(),
                campanha.getTitle(),
                campanha.getDescription(),
                raca.getName(),
                characterClass.getName()
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> payload = new HashMap<>();
        payload.put("model", "local-model");
        payload.put("messages", List.of(
                Map.of("role", "user", "content", prompt)
        ));

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

        Map<String, Object> choice = ((List<Map<String, Object>>) response.getBody().get("choices")).get(0);
        Map<String, String> message = (Map<String, String>) choice.get("message");
        String conteudo = message.get("content");

        // Extrair nome do personagem
        String nomeExtraido = "Personagem Sem Nome";
        if (conteudo.startsWith("Nome:")) {
            int fimDaLinha = conteudo.indexOf("\n");
            nomeExtraido = conteudo.substring(6, fimDaLinha).trim();
        }

        // Criar e salvar o personagem
        CharacterDnd novoPersonagem = new CharacterDnd();
        novoPersonagem.setNome(nomeExtraido);
        novoPersonagem.setHistoria(extrairHistoriaLimpa(conteudo));
        novoPersonagem.setNivel(1);
        novoPersonagem.setXp(0);
        novoPersonagem.setCampanha(campanha);
        novoPersonagem.setRaca(raca);
        novoPersonagem.setCharacterClass(characterClass);

        // Atributos aleatórios entre 8 e 18 (padrão D&D)
        novoPersonagem.setForca(rolarAtributo());
        novoPersonagem.setDestreza(rolarAtributo());
        novoPersonagem.setConstituicao(rolarAtributo());
        novoPersonagem.setInteligencia(rolarAtributo());
        novoPersonagem.setSabedoria(rolarAtributo());
        novoPersonagem.setCarisma(rolarAtributo());

        CharacterDnd personagemSalvo = characterDndService.create(novoPersonagem);
        return ResponseEntity.ok(personagemSalvo);
    }

    private int rolarAtributo() {
        return 8 + (int)(Math.random() * 11); // 8 a 18
    }



    public String extrairHistoriaLimpa(String respostaIA) {
        if (respostaIA.contains("</think>")) {
            return respostaIA.substring(respostaIA.indexOf("</think>") + 8).trim();
        }
        return respostaIA.trim(); // caso não haja <think>
    }

}

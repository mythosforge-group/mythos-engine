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

    public CharacterGenerationController(
            CampaignService campaignService,
            RaceService raceService,
            CharacterClassService classService,
            SystemService systemService
    ) {
        this.campaignService = campaignService;
        this.raceService = raceService;
        this.classService = classService;
        this.systemService = systemService;
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
            Gere a história de um personagem para um RPG baseado em Dungeons and Dragons.
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
}

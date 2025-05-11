package mythosforge.fable_minds.controller;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class DeepSeekController {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String url = "http://localhost:1234/v1/chat/completions";


    @PostMapping("/deepseek")
    public String perguntarParaIA(@RequestBody String prompt) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);


        Map<String, Object> payload = new HashMap<>();
        payload.put("model", "local-model"); // pode ser qualquer nome, o LM Studio ignora
        payload.put("messages", List.of(
                Map.of("role", "user", "content", prompt)
        ));

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

        Map<String, Object> choice = ((List<Map<String, Object>>) response.getBody().get("choices")).get(0);
        Map<String, String> message = (Map<String, String>) choice.get("message");
        return message.get("content");
    }
}


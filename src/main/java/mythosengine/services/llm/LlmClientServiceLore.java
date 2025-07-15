package mythosengine.services.llm;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LlmClientServiceLore {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String url = "http://localhost:1234/v1/chat/completions";

    public String request(String prompt) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String,Object> payload = new HashMap<>();
        payload.put("model", "local-model");
        payload.put("messages", List.of(Map.of("role","user","content",prompt)));

        HttpEntity<Map<String,Object>> request = new HttpEntity<>(payload, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

        Map<String,Object> choice = ((List<Map<String,Object>>)response.getBody().get("choices")).get(0);
        Map<String,String> message = (Map<String,String>)choice.get("message");
        return message.get("content");
    }

    @SuppressWarnings("unchecked")
    public String requestChat(List<Map<String,String>> messages) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String,Object> payload = new HashMap<>();
        payload.put("model", "local-model");
        payload.put("messages", messages);
        payload.put("stop", List.of("Jogador:"));

        HttpEntity<Map<String,Object>> req = new HttpEntity<>(payload, headers);
        ResponseEntity<Map> resp = restTemplate.postForEntity(url, req, Map.class);

        Map<String,Object> choice = ((List<Map<String,Object>>)resp.getBody().get("choices")).get(0);
        Map<String,String> message = (Map<String,String>)choice.get("message");
        return message.get("content").trim();
    }
}
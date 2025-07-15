package mythosforge.chronicle_architect.llm;

import mythosengine.services.llm.GeminiClientService;
import org.springframework.stereotype.Service;

/**
 * Cliente LLM dedicado para o Chronicle Architect.
 * Atua como uma fachada (Facade) para o serviço Gemini
 */
@Service
public class LlmClientServiceArchitect {

    private final GeminiClientService geminiClient;

    public LlmClientServiceArchitect(GeminiClientService geminiClient) {
        this.geminiClient = geminiClient;
    }

    public String generateContent(String prompt) {
        return geminiClient.generateContent(prompt);
    }
}
package mythosengine.services.llm;

import com.google.genai.Client;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class GeminiClientService {

    private final Client client;
    private final String modelName = "gemini-1.5-flash"; // Definimos o nome do modelo aqui

    /**
     * Construtor que inicializa o cliente do Gemini usando injeção de dependência.
     * Isso garante que a 'apiKey' não será nula durante a inicialização.
     *
     * @param apiKey A chave da API do Google Gemini, injetada pelo Spring.
     */
    public GeminiClientService(@Value("${gemini.api-key}") String apiKey) {
        this.client = new Client.Builder()
            .apiKey(apiKey)
            .build();
    }

    /**
     * Envia um prompt de texto para o modelo Gemini e retorna a resposta.
     *
     * @param promptText O prompt para gerar o conteúdo.
     * @return O texto gerado pelo modelo.
     */
    public String generateContent(String promptText) {
        try {
            var response = this.client.models.generateContent(this.modelName, promptText, null);
            return response.text();

        } catch (Exception e) {
            System.err.println("Erro ao gerar conteúdo com o Gemini: " + e.getMessage());
            return "Erro ao gerar conteúdo. Tente novamente.";
        }
    }
}
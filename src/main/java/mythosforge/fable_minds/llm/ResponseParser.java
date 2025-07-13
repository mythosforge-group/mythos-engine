package mythosforge.fable_minds.llm;

public class ResponseParser {

    public static String extrairHistoriaLimpa(String respostaIA) {
        if (respostaIA.contains("</think>")) {
            return respostaIA.substring(respostaIA.indexOf("</think>") + 8).trim();
        }
        return respostaIA.trim();
    }

    public static String extrairNome(String historia) {
        for (String linha : historia.split("\n")) {
            String linhaLimpa = linha.trim()
                    .replace("**", "")
                    .replace("*", "")
                    .replace(":", "")
                    .toLowerCase();

            if (linhaLimpa.startsWith("nome")) {
                String[] partes = linha.split(":", 2);
                if (partes.length > 1) {
                    return partes[1].replace("**", "").trim();
                }
            }
        }
        return "Personagem sem nome";
    }

    public static String extrairNpcResponse(String raw) {
        return raw.trim();
    }


    public static String extractContentAfterThinkBlock(String rawResponse) {
        if (rawResponse == null) {
            return "";
        }

        String endTag = "</think>";
        int endIndex = rawResponse.lastIndexOf(endTag);

        if (endIndex != -1) {
            // Retorna o texto que vem DEPOIS da tag </think>, removendo espaços em branco.
            return rawResponse.substring(endIndex + endTag.length()).trim();
        }

        // Se não houver um bloco <think>, retorna a resposta inteira, apenas por segurança.
        return rawResponse.trim();
    }
}

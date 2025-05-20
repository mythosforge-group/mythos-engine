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
}
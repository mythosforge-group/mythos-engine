package mythosforge.chronicle_architect.llm;

/**
 * Responsável por limpar e formatar as respostas brutas da LLM
 * para o contexto do Chronicle Architect.
 */
public class ResponseParserArchitect {

    /**
     * Limpeza básica. Remove espaços em branco desnecessários.
     * Pode ser expandido para extrair blocos de código ou outras estruturas.
     * @param rawResponse A resposta completa da LLM.
     * @return O texto limpo.
     */
    public static String cleanResponse(String rawResponse) {
        if (rawResponse == null) {
            return "";
        }
        return rawResponse.trim();
    }
}
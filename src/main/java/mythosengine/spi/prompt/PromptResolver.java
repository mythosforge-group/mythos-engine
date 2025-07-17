package mythosengine.spi.prompt;

import mythosengine.spi.content.ContentGenerationContext;

/**
 * Define o contrato para resolver (montar) o prompt final a ser enviado para a LLM.
 * Esta é a abstração para o sistema de templates. A implementação concreta
 * (usando YAML, Mustache, ou qualquer outra técnica) é responsabilidade da aplicação.
 *
 * @version 2.0.0
 */
public interface PromptResolver {

    /**
     * Verifica se este resolver é capaz de lidar com o contexto de geração atual.
     * Um resolver pode ser específico para um tipo de geração (ex: "HISTORIA_PERSONAGEM")
     * ou para um sistema de jogo (ex: "D&D 5e").
     *
     * @param context O contexto da geração contendo tipo e parâmetros.
     * @return {@code true} se este resolver for compatível com o contexto, {@code false} caso contrário.
     */
    boolean supports(ContentGenerationContext context);

    /**
     * Monta o prompt final com base nos dados e regras do contexto.
     *
     * @param context O contexto com todos os parâmetros necessários para a montagem.
     * @return Uma string contendo o prompt pronto para ser enviado à LLM.
     * @throws IllegalStateException se o contexto não contiver os parâmetros necessários.
     */
    String resolve(ContentGenerationContext context);
}
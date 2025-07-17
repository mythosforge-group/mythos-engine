package mythosengine.spi.prompt;

import mythosengine.spi.content.ContentGenerationContext;

/**
 * Define o contrato para classes que constroem a string de um prompt
 * a partir de um contexto de geração. Esta é a interface principal
 * para o ponto flexível de adaptação de conteúdo do framework.
 *
 * @version 3.0.0
 */
public interface PromptBuilder {

    /**
     * Verifica se este construtor é capaz de lidar com o contexto de geração atual,
     * geralmente verificando o 'generationType'.
     *
     * @param context O contexto da geração contendo o tipo e os parâmetros.
     * @return {@code true} se este construtor for compatível com o contexto, {@code false} caso contrário.
     */
    boolean supports(ContentGenerationContext context);

    /**
     * Constrói o prompt final com base nos dados e regras do contexto.
     *
     * @param context O contexto com todos os parâmetros necessários para a montagem.
     * @return Uma string contendo o prompt pronto para ser enviado à LLM.
     * @throws IllegalStateException se o contexto não contiver os parâmetros necessários.
     */
    String build(ContentGenerationContext context);
}
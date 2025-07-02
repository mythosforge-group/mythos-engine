package mythosengine.core.modules.content;

import mythosengine.core.modules.IModule;

public interface IContentGeneratorModule extends IModule {
    /**
     * Verifica se este módulo é capaz de lidar com um determinado tipo de geração.
     * @param context O contexto da geração.
     * @return true se o módulo for compatível, false caso contrário.
    */
    boolean supports(ContentGenerationContext context);

    /**
     * Gera o conteúdo com base no contexto fornecido.
     * @param context O objeto com todos os parâmetros para a geração.
     * @return Um objeto com o conteúdo gerado.
    */
    GeneratedContent generate(ContentGenerationContext context);
}
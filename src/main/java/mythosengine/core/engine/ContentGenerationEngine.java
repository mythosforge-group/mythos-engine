package mythosengine.core.engine;

import org.springframework.stereotype.Service;

import mythosengine.spi.content.ContentGenerationContext;
import mythosengine.spi.content.GeneratedContent;
import mythosengine.spi.content.IContentGeneratorModule;

import java.util.List;

@Service
public class ContentGenerationEngine {

    private final List<IContentGeneratorModule> availableModules;

    /**
     * Inicializa o mecanismo de geração de conteúdo com os módulos disponíveis.
     * @param availableModules Lista de módulos de geração de conteúdo carregados.
     */
    public ContentGenerationEngine(List<IContentGeneratorModule> availableModules) {
        this.availableModules = availableModules;
        System.out.println("FRAMEWORK_ENGINE: Módulos de geração de conteúdo carregados: " + availableModules.size());
    }
    
    /**
     * Processa a geração de conteúdo com base no contexto fornecido.
     * @param context O contexto de geração que contém o tipo e os parâmetros necessários.
     * @return O conteúdo gerado pelo módulo apropriado.
    */
    public GeneratedContent process(ContentGenerationContext context) {
        IContentGeneratorModule module = availableModules.stream()
            .filter(m -> m.supports(context))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Nenhum módulo de geração de conteúdo encontrado para o tipo: " + context.getGenerationType()));

        System.out.println("FRAMEWORK_ENGINE: Usando o módulo '" + module.getModuleName() + "' para a geração.");
        return module.generate(context);
    }
}
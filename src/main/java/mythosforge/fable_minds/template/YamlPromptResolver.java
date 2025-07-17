package mythosforge.fable_minds.template;

import mythosengine.spi.content.ContentGenerationContext;
import mythosengine.spi.prompt.PromptResolver;
import mythosforge.fable_minds.models.Campaign;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementação concreta de {@link PromptResolver} que utiliza arquivos YAML
 * para buscar e processar templates de prompt.
 * Esta classe é um PONTO VARIÁVEL pertencente à aplicação.
 */
@Component
public class YamlPromptResolver implements PromptResolver {

    private final ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
    private final ResourceLoader resourceLoader;
    private final Map<String, RpgTemplate> templateCache = new ConcurrentHashMap<>();

    public YamlPromptResolver(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public boolean supports(ContentGenerationContext context) {
        String location = buildTemplateLocation(context);
        if (location == null) {
            return false;
        }
        Resource resource = resourceLoader.getResource(location);
        return resource.exists();
    }

    @Override
    public String resolve(ContentGenerationContext context) {
        RpgTemplate template = getTemplate(context);
        return processTemplate(template, context.getParameters());
    }

    private RpgTemplate getTemplate(ContentGenerationContext context) {
        String cacheKey = buildTemplateLocation(context);
        if (templateCache.containsKey(cacheKey)) {
            return templateCache.get(cacheKey);
        }

        try {
            Resource resource = resourceLoader.getResource(cacheKey);
            if (!resource.exists()) {
                throw new IllegalStateException("Template não encontrado em: " + cacheKey);
            }
            try (InputStream inputStream = resource.getInputStream()) {
                RpgTemplate template = objectMapper.readValue(inputStream, RpgTemplate.class);
                templateCache.put(cacheKey, template);
                return template;
            }
        } catch (Exception e) {
            throw new RuntimeException("Falha ao carregar o template: " + cacheKey, e);
        }
    }

    private String buildTemplateLocation(ContentGenerationContext context) {
        Campaign campaign = (Campaign) context.getParameters().get("campaign");
        if (campaign == null || campaign.getSystem() == null) {
            return null;
        }
        // Exemplo de caminho: "classpath:templates/rpg_systems/dnd_5e/historia_personagem.yaml"
        return String.format("classpath:templates/rpg_systems/%s/%s.yaml",
                campaign.getSystem().getName().toLowerCase().replace(" ", "_"),
                context.getGenerationType().toLowerCase());
    }

    private String processTemplate(RpgTemplate template, Map<String, Object> contextData) {
        String processedPrompt = template.getPrompt();

        // Substitui todas as chaves encontradas no mapa de dados.
        for (Map.Entry<String, Object> entry : contextData.entrySet()) {
            // Suporte para acessar propriedades de objetos (ex: {{campaign.title}})
            if (entry.getValue() instanceof Campaign) {
                 Campaign campaign = (Campaign) entry.getValue();
                 processedPrompt = processedPrompt.replace("{{campaign.title}}", campaign.getTitle());
                 processedPrompt = processedPrompt.replace("{{campaign.description}}", campaign.getDescription());
                 processedPrompt = processedPrompt.replace("{{system.name}}", campaign.getSystem().getName());
            } else {
                 processedPrompt = processedPrompt.replace("{{" + entry.getKey() + "}}", entry.getValue().toString());
            }
        }

        if (template.getRules() != null && !template.getRules().isEmpty()) {
            processedPrompt = processedPrompt.replace("{{rules}}", String.join("\n- ", template.getRules()));
        }

        return processedPrompt;
    }
}
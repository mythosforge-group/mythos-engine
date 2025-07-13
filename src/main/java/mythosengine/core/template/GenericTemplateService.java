

package mythosengine.core.template;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class GenericTemplateService {

    private final ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
    private final ResourceLoader resourceLoader;
    private final Map<String, RpgTemplate> templateCache = new ConcurrentHashMap<>();

    public GenericTemplateService(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    /**
     * Carrega um template genérico de um tipo e nome específicos.
     * @param templateType O diretório principal (ex: "rpg_systems", "lore_weaver").
     * @param templateName O nome do ficheiro .yaml (ex: "dnd/historia_personagem", "expand_article").
     */
    public RpgTemplate getTemplate(String templateType, String templateName) {
        String cacheKey = templateType + ":" + templateName;
        if (templateCache.containsKey(cacheKey)) {
            return templateCache.get(cacheKey);
        }

        try {
            String location = "classpath:templates/" + templateType + "/" + templateName + ".yaml";
            Resource resource = resourceLoader.getResource(location);
            if (!resource.exists()) {
                throw new RuntimeException("Template não encontrado em: " + location);
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


    public boolean templateExists(String templateType, String templateName) {
        try {
            String location = "classpath:templates/" + templateType + "/" + templateName + ".yaml";
            Resource resource = resourceLoader.getResource(location);
            return resource.exists();
        } catch (Exception e) {
            return false;
        }
    }


    public String processTemplate(RpgTemplate template, Map<String, Object> contextData) {
        String processedPrompt = template.getPrompt();

        // Substitui todas as chaves encontradas no mapa de dados.
        for (Map.Entry<String, Object> entry : contextData.entrySet()) {
            processedPrompt = processedPrompt.replace("{{" + entry.getKey() + "}}", entry.getValue().toString());
        }

        if (template.getRules() != null && !template.getRules().isEmpty()) {
            processedPrompt = processedPrompt.replace("{{rules}}", String.join("\n- ", template.getRules()));
        }

        return processedPrompt;
    }
}

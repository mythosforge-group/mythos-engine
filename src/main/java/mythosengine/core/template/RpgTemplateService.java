package mythosengine.core.template;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.springframework.stereotype.Service;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RpgTemplateService {

    private final ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
    private final ResourceLoader resourceLoader;
    private final Map<String, RpgTemplate> templateCache = new ConcurrentHashMap<>();

    public RpgTemplateService(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    /**
     * Carrega um template com base no nome do sistema e no tipo de geração.
     * Ex: systemName="tormenta_20", generationType="historia_personagem"
     */
    public RpgTemplate getTemplate(String systemName, String generationType) {
        String cacheKey = systemName + "_" + generationType;
        if (templateCache.containsKey(cacheKey)) {
            return templateCache.get(cacheKey);
        }

        try {
            String location = "classpath:rpg_templates/" + systemName.toLowerCase().replace(" ", "_") + "/" + generationType.toLowerCase() + ".yaml";
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

    /**
     * Preenche o prompt do template com os dados do contexto.
     */
    public String processTemplate(RpgTemplate template, Map<String, Object> contextData) {
        String processedPrompt = template.getPrompt();
        
        // Lógica simples de substituição. Pode ser trocada por um motor de template como Mustache.
        processedPrompt = processedPrompt.replace("{{system.name}}", contextData.get("systemName").toString());
        processedPrompt = processedPrompt.replace("{{campaign.title}}", contextData.get("campaignTitle").toString());
        processedPrompt = processedPrompt.replace("{{campaign.description}}", contextData.get("campaignDescription").toString());
        processedPrompt = processedPrompt.replace("{{race.name}}", contextData.get("raceName").toString());
        processedPrompt = processedPrompt.replace("{{characterClass.name}}", contextData.get("className").toString());
        processedPrompt = processedPrompt.replace("{{rules}}", String.join("\n- ", template.getRules()));

        return processedPrompt;
    }
}
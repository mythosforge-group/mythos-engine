package mythosforge.chronicle_architect.llm;

import mythosforge.chronicle_architect.model.Rulebook;
import mythosforge.chronicle_architect.model.SpellComponent;
import mythosengine.core.template.GenericTemplateService;
import mythosengine.core.template.RpgTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * Constrói os prompts específicos para as funcionalidades do Chronicle Architect,
 * utilizando o sistema de templates do framework.
 */
public class PromptBuilderArchitect {

    public static String buildChapterSuggestionPrompt(GenericTemplateService templateService, Rulebook rulebook) {
        RpgTemplate template = templateService.getTemplate("chronicle_architect", "suggest_chapters");

        Map<String, Object> templateData = new HashMap<>();
        templateData.put("book.title", rulebook.getName());
        templateData.put("book.description", rulebook.getDescription());

        return templateService.processTemplate(template, templateData);
    }

    public static String buildSpellGenerationPrompt(GenericTemplateService templateService, SpellComponent spell) {
        RpgTemplate template = templateService.getTemplate("chronicle_architect", "generate_spell");

        Map<String, Object> templateData = new HashMap<>();
        templateData.put("spell.name", spell.getName());
        templateData.put("spell.school", spell.getSchool());
        templateData.put("spell.level", spell.getLevel());

        return templateService.processTemplate(template, templateData);
    }
}
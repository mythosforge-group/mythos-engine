package mythosforge.chronicle_architect.modules;

import mythosengine.core.modules.content.ContentGenerationContext;
import mythosengine.core.modules.content.GeneratedContent;
import mythosengine.core.template.GenericTemplateService;
import mythosengine.core.template.RpgTemplate;
import mythosforge.chronicle_architect.llm.LlmClientServiceArchitect;
import mythosforge.chronicle_architect.model.Rulebook;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookStructureModuleTest {

    @Mock
    private LlmClientServiceArchitect llmClient; // Mock da dependência externa

    @Mock
    private GenericTemplateService templateService; // Mock da dependência do framework

    @InjectMocks
    private BookStructureModule bookStructureModule; // A classe que estamos testando

    @Test
    void supports_shouldReturnTrue_forSupportedType() {
        // Arrange
        ContentGenerationContext context = ContentGenerationContext.builder()
                .generationType("BOOK_SUGGEST_CHAPTERS")
                .build();

        // Act & Assert
        assertTrue(bookStructureModule.supports(context));
    }

    @Test
    void supports_shouldReturnFalse_forUnsupportedType() {
        // Arrange
        ContentGenerationContext context = ContentGenerationContext.builder()
                .generationType("HISTORIA_PERSONAGEM") // Tipo não suportado por este módulo
                .build();

        // Act & Assert
        assertFalse(bookStructureModule.supports(context));
    }

    @Test
    void generate_shouldReturnGeneratedContent_forChapterSuggestions() {
        // Arrange
        Rulebook testBook = new Rulebook();
        testBook.setName("O Guia do Aventureiro");
        testBook.setDescription("Um manual completo para heróis.");

        ContentGenerationContext context = ContentGenerationContext.builder()
                .generationType("BOOK_SUGGEST_CHAPTERS")
                .parameters(Map.of("rulebook", testBook))
                .build();

        String llmResponse = "1. Introdução\n2. Criação de Personagem\n3. Magias e Feitiços";

        // Simula o comportamento dos mocks
        when(templateService.getTemplate(anyString(), anyString())).thenReturn(new RpgTemplate());
        when(templateService.processTemplate(any(), any())).thenReturn("Prompt Processado");
        when(llmClient.request(anyString())).thenReturn(llmResponse);

        // Act
        GeneratedContent result = bookStructureModule.generate(context);

        // Assert
        assertNotNull(result);
        assertEquals(llmResponse, result.getMainText().trim());
    }
}
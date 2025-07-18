package mythosforge.fable_minds.modules;

import mythosforge.fable_minds.llm.LlmClientService;
import mythosforge.fable_minds.models.Campaign;
import mythosforge.fable_minds.models.CharacterClass;
import mythosforge.fable_minds.models.Race;
import mythosforge.fable_minds.models.System;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import mythosengine.spi.content.ContentGenerationContext;
import mythosengine.spi.content.GeneratedContent;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MythosForgeStoryGeneratorModuleTest {

    @Mock
    private LlmClientService llmClient; // Mockamos a dependência externa

    @InjectMocks
    private MythosForgeStoryGeneratorModule storyGenerator; // A classe que estamos a testar

    private Campaign campaign;
    private Race race;
    private CharacterClass characterClass;

    @BeforeEach
    void setUp() {
        // Preparamos dados de teste falsos
        System system = new System();
        system.setName("D&D 5e");

        campaign = new Campaign();
        campaign.setTitle("A Ameaça Subterrânea");
        campaign.setDescription("Uma antiga maldição desperta nas profundezas.");
        campaign.setSystem(system);

        race = new Race();
        race.setName("Anão da Montanha");

        characterClass = new CharacterClass();
        characterClass.setName("Guerreiro");
    }

    @Test
    void supports_deveRetornarTrue_paraTipoSuportado() {
        // Arrange
        ContentGenerationContext context = ContentGenerationContext.builder()
                .generationType("HISTORIA_PERSONAGEM")
                .build();

        // Act & Assert
        assertTrue(storyGenerator.supports(context));
    }

    @Test
    void supports_deveRetornarFalse_paraTipoNaoSuportado() {
        // Arrange
        ContentGenerationContext context = ContentGenerationContext.builder()
                .generationType("LORE_CULTURA") // Tipo não suportado por este módulo
                .build();

        // Act & Assert
        assertFalse(storyGenerator.supports(context));
    }

    @Test
    void generate_deveGerarConteudoDoPersonagem_comSucesso() {
        // Arrange
        String respostaFalsaDaLLM = "Nome: Thorin Escudo-de-Carvalho\n\n**História:** Thorin é um nobre anão exilado...";
        when(llmClient.request(anyString())).thenReturn(respostaFalsaDaLLM);

        ContentGenerationContext context = ContentGenerationContext.builder()
                .generationType("HISTORIA_PERSONAGEM")
                .parameters(Map.of(
                        "campaign", campaign,
                        "race", race,
                        "class", characterClass
                ))
                .build();

        // Act
        GeneratedContent resultado = storyGenerator.generate(context);

        // Assert
        assertNotNull(resultado);
        assertEquals("Thorin Escudo-de-Carvalho", resultado.getMetadata().get("nome"));
        assertTrue(resultado.getMainText().contains("nobre anão exilado"));
    }
}
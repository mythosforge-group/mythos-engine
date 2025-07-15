package mythosforge.chronicle_architect.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import mythosengine.services.lineage.visualizer.GraphVisualizer;
import mythosforge.chronicle_architect.llm.LlmClientServiceArchitect;
import mythosforge.chronicle_architect.model.Rulebook;
import mythosforge.chronicle_architect.model.SkillComponent;
import mythosforge.chronicle_architect.service.RuleComponentService;
import mythosforge.chronicle_architect.service.RulebookService;
import mythosforge.fable_minds.FableMindsApplication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = FableMindsApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("Testes de Integração para Chronicle Architect")
public class ArchitectControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // --- MUDANÇA 1: Injetar os SERVIÇOS em vez dos Repositórios para o setup ---
    @Autowired
    private RulebookService rulebookService;

    @Autowired
    private RuleComponentService ruleComponentService;

    @MockitoBean
    private LlmClientServiceArchitect llmClient;

    @MockitoBean
    private GraphVisualizer graphVisualizer;

    private Rulebook testRulebook;
    private SkillComponent skill1;
    private SkillComponent skill2;

    @BeforeEach
    void setupDatabase() {
        Rulebook newRulebook = new Rulebook();
        newRulebook.setName("Crônicas de Arton");
        newRulebook.setDescription("Um sistema de fantasia medieval.");
        newRulebook.setVersion("1.0");
        newRulebook.setAuthor("Equipe Mythos Forge");
        this.testRulebook = rulebookService.create(newRulebook);

        SkillComponent newSkill1 = new SkillComponent();
        newSkill1.setName("Ataque Poderoso");
        newSkill1.setDescription("Aumenta o dano do ataque.");
        newSkill1.setTags("Combate, Corpo-a-corpo"); // Inicializando o campo
        this.skill1 = ruleComponentService.create(newSkill1);

        SkillComponent newSkill2 = new SkillComponent();
        newSkill2.setName("Ataque Poderoso Aprimorado");
        newSkill2.setDescription("Aumenta ainda mais o dano do ataque.");
        newSkill2.setTags("Combate, Aprimoramento"); // Inicializando o campo
        this.skill2 = ruleComponentService.create(newSkill2);
    }

    @Test
    @DisplayName("Deve sugerir capítulos para um livro via endpoint")
    void shouldSuggestChaptersForABook() throws Exception {
        String mockedChapters = "1. Introdução ao Mundo\n2. Raças e Classes\n3. Sistema de Combate";
        when(llmClient.request(anyString())).thenReturn(mockedChapters);

        mockMvc.perform(post("/api/architect/rulebooks/{id}/suggest-chapters", testRulebook.getId()))
                .andExpect(status().isOk())
                .andExpect(content().string(mockedChapters));
    }

    @Test
    @DisplayName("Deve criar pré-requisito e gerar grafo de dependência")
    void shouldCreatePrerequisiteAndGenerateGraph() throws Exception {
        mockMvc.perform(post("/api/architect/components/{componentId}/prerequisite/{prerequisiteId}",
                        skill2.getId(), skill1.getId()))
                .andExpect(status().isOk());

        byte[] fakeImageBytes = "fake-png-data".getBytes();
        when(graphVisualizer.visualize(any(), any())).thenReturn(fakeImageBytes);

        mockMvc.perform(get("/api/architect/components/{id}/dependency-graph", skill2.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_PNG))
                .andExpect(content().bytes(fakeImageBytes));
    }

    @Test
    @DisplayName("Deve criar um novo SkillComponent via endpoint")
    void shouldCreateNewSkillComponent() throws Exception {
        SkillComponent newSkill = new SkillComponent();
        newSkill.setName("Furtividade");
        newSkill.setDescription("Permite mover-se sem ser detectado.");
        newSkill.setTags("Exploração, Destreza");

        mockMvc.perform(post("/api/architect/components/skill")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newSkill)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Furtividade")))
                .andExpect(jsonPath("$.tags", is("Exploração, Destreza")));
    }
}
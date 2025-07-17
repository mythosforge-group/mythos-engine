package mythosforge.fable_minds.controller;

import mythosforge.fable_minds.llm.LlmClientService;
import mythosforge.fable_minds.models.Campaign;
import mythosforge.fable_minds.models.CharacterClass;
import mythosforge.fable_minds.models.Race;
import mythosforge.fable_minds.models.System;
import mythosforge.fable_minds.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser; // Import adicionado
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import mythosengine.security.service.auth.models.Users;
import mythosengine.security.service.auth.repository.UserRepository;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class CharacterGenerationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LlmClientService llmClient;

    @Autowired private CampaignRepository campaignRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private SystemRepository systemRepository;
    @Autowired private RaceRepository raceRepository;
    @Autowired private CharacterClassRepository classRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    private Long campaignId, raceId, classId;

    @BeforeEach
    void setupDatabase() {
        Users user = new Users();
        user.setUsername("tester");
        user.setPassword(passwordEncoder.encode("password"));
        user.setEmail("tester@test.com");
        user = userRepository.save(user);

        System system = new System();
        system.setName("D&D 5e");
        system.setDescription("Sistema de teste");
        system = systemRepository.save(system);

        Campaign campaign = new Campaign();
        campaign.setTitle("Test Campaign");
        campaign.setDescription("A test desc");
        campaign.setUser(user);
        campaign.setSystem(system);
        campaignId = campaignRepository.save(campaign).getId();

        Race race = new Race();
        race.setName("Elfo");
        race.setDescription("");
        race.setSystem(system);
        raceId = raceRepository.save(race).getId();

        CharacterClass characterClass = new CharacterClass();
        characterClass.setName("Mago");
        characterClass.setDescription("");
        characterClass.setSystem(system);
        classId = classRepository.save(characterClass).getId();
    }

    @Test
    @WithMockUser
    void deveGerarESalvarPersonagemComSucesso() throws Exception {
        // Arrange
        String respostaCompletaDaLLM = "Nome: Elara\n\nHistória: Uma maga elfa em busca de conhecimento arcano.";
        when(llmClient.request(anyString())).thenReturn(respostaCompletaDaLLM);

        // Act & Assert
        mockMvc.perform(post("/api/personagens/dnd/gerar-e-salvar")
                .param("campaignId", campaignId.toString())
                .param("raceId", raceId.toString())
                .param("classId", classId.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Elara"))
                .andExpect(jsonPath("$.historia").value(respostaCompletaDaLLM));
    }
}

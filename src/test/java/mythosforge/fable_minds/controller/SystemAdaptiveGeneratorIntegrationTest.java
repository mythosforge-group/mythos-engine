package mythosforge.fable_minds.controller;

import mythosforge.fable_minds.llm.LlmClientService;
import mythosforge.fable_minds.models.Campaign;
import mythosforge.fable_minds.models.CharacterClass;
import mythosforge.fable_minds.models.Race;
import mythosforge.fable_minds.models.System;
import mythosforge.fable_minds.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
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
@DisplayName("Testes de Integração para o Módulo de Geração Adaptativo")
public class SystemAdaptiveGeneratorIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean // Mockamos o cliente da LLM para não fazer chamadas externas reais
    private LlmClientService llmClient;

    // Injetamos os repositórios para popular o banco de dados de teste
    @Autowired private CampaignRepository campaignRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private SystemRepository systemRepository;
    @Autowired private RaceRepository raceRepository;
    @Autowired private CharacterClassRepository classRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    private Long campaignId, raceId, classId;

    @BeforeEach
    void setupDatabase() {
        // 1. Criar um usuário
        Users user = new Users();
        user.setUsername("testerSebastiao");
        user.setPassword(passwordEncoder.encode("framework123"));
        user.setEmail("sebastiao@mythosforge.com");
        user = userRepository.save(user);

        // 2. Criar o sistema "Tormenta 20", que deve corresponder ao nome no template YAML
        System system = new System();
        system.setName("Tormenta 20");
        system.setDescription("Um mundo de fantasia medieval cheio de perigos e heróis.");
        system = systemRepository.save(system);

        // 3. Criar uma campanha dentro do sistema Tormenta 20
        Campaign campaign = new Campaign();
        campaign.setTitle("O Reinado da Tormenta");
        campaign.setDescription("Uma praga profana avança sobre o reino de Deheon.");
        campaign.setUser(user);
        campaign.setSystem(system);
        campaignId = campaignRepository.save(campaign).getId();

        // 4. Criar Raça e Classe do universo de Tormenta 20
        Race race = new Race();
        race.setName("Lefou");
        race.setDescription("Corrompidos pela Tormenta, mas lutando para manter sua humanidade.");
        race.setSystem(system);
        raceId = raceRepository.save(race).getId();

        CharacterClass characterClass = new CharacterClass();
        characterClass.setName("Inventor");
        characterClass.setDescription("Mestre de engenhocas, alquimia e explosivos.");
        characterClass.setSystem(system);
        classId = classRepository.save(characterClass).getId();
    }

    @Test
    @WithMockUser // Simula um usuário autenticado para passar pela segurança do Spring
    @DisplayName("Deve gerar um personagem de Tormenta 20 usando o template YAML correspondente")
    void deveGerarPersonagemDeTormenta20ComSucesso() throws Exception {
        // Arrange: Simular a resposta que a LLM nos daria
        String respostaDaLLM = "Nome: Thorkell\n\nHistória: Um inventor Lefou que busca uma cura para a corrupção da Tormenta usando a ciência.";
        when(llmClient.request(anyString())).thenReturn(respostaDaLLM);

        // Act & Assert: Fazemos a chamada ao endpoint e verificamos a resposta
        mockMvc.perform(post("/api/personagens/dnd/gerar-e-salvar") // O endpoint continua o mesmo
                .param("campaignId", campaignId.toString())
                .param("raceId", raceId.toString())
                .param("classId", classId.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Thorkell"))
                .andExpect(jsonPath("$.historia").value(respostaDaLLM))
                .andExpect(jsonPath("$.raca.name").value("Lefou"))
                .andExpect(jsonPath("$.characterClass.name").value("Inventor"));
    }
}
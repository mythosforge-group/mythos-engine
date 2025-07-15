package mythosforge.chronicle_architect.controller;

import mythosengine.services.llm.GeminiClientService;
import mythosforge.chronicle_architect.models.Book;
import mythosforge.chronicle_architect.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
public class ChronicleArchitectControllerTest {

    @TestConfiguration
    static class TestConfig {
        @Bean
        @Primary
        public GeminiClientService geminiClientServiceMock() {
            return mock(GeminiClientService.class);
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private GeminiClientService geminiClient;

    private Book testBook;

    @BeforeEach
    void setUp() {
        Book book = new Book();
        book.setTitle("Crônicas de Valéria");
        book.setDescription("Um mundo de fantasia assolado por um inverno sem fim.");
        book.setEntityId(UUID.randomUUID());
        testBook = bookRepository.saveAndFlush(book);
    }

    @Test
    @WithMockUser
    void deveSugerirEstruturaDeCapitulosComSucesso() throws Exception {
        // Arrange
        String respostaEsperada = "Capítulo 1: O Inverno Eterno\nCapítulo 2: O Despertar";
        when(geminiClient.generateContent(anyString())).thenReturn(respostaEsperada);

        // Act & Assert
        mockMvc.perform(post("/api/chronicle/books/" + testBook.getId() + "/suggest-chapters")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(respostaEsperada));
    }

    @Test
    @WithMockUser
    void deveSugerirGlossarioComSucesso() throws Exception {
        // Arrange
        String respostaEsperada = "Gelo Perpétuo: Gelo que nunca derrete.\nValéria: O continente principal da campanha.";
        when(geminiClient.generateContent(anyString())).thenReturn(respostaEsperada);

        // Act & Assert
        mockMvc.perform(post("/api/chronicle/books/" + testBook.getId() + "/suggest-glossary")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(respostaEsperada));
    }
}
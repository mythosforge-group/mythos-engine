package mythosforge.fable_minds.service;

import mythosforge.fable_minds.dto.NpcConversationResponseDTO;
import mythosforge.fable_minds.dto.NpcMessageResponseDTO;
import mythosforge.fable_minds.llm.LlmClientService;
import mythosforge.fable_minds.models.NpcConversation;
import mythosforge.fable_minds.models.NpcMessage;
import mythosforge.fable_minds.repository.NpcConversationRepository;
import mythosforge.fable_minds.repository.NpcMessageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class NpcInteractionService {

    private final NpcConversationRepository conversationRepo;
    private final NpcMessageRepository messageRepo;
    private final LlmClientService llmClient;

    public NpcInteractionService(
            NpcConversationRepository conversationRepo,
            NpcMessageRepository messageRepo,
            LlmClientService llmClient
    ) {
        this.conversationRepo = conversationRepo;
        this.messageRepo = messageRepo;
        this.llmClient = llmClient;
    }

    public NpcConversationResponseDTO createConversation(String npcName, String descricao) {
        NpcConversation conv = new NpcConversation();
        conv.setNpcName(npcName);
        conv.setDescricao(descricao);
        conv = conversationRepo.save(conv);

        String systemPrompt = buildSystemPrompt(npcName, descricao);
        String initialReply = llmClient.requestChat(
            List.of(Map.of("role","system","content", systemPrompt))
        );

        NpcMessage npcMsg = new NpcMessage();
        npcMsg.setRole("npc");
        npcMsg.setContent(initialReply);
        npcMsg.setConversation(conv);
        messageRepo.save(npcMsg);

        return toConversationDto(conv);
    }

    @Transactional
    public String sendUserMessage(Long conversationId, String userContent) {
        NpcConversation conv = conversationRepo.findById(conversationId)
            .orElseThrow(() -> new IllegalArgumentException("Conversa não encontrada"));

        NpcMessage userMsg = new NpcMessage();
        userMsg.setRole("user");
        userMsg.setContent(userContent);
        userMsg.setConversation(conv);
        messageRepo.save(userMsg);
 
        String systemPrompt = buildSystemPrompt(conv.getNpcName(), conv.getDescricao());
        String userPrompt   = buildUserPrompt(conv.getNpcName(), conv.getMessages(), userContent);

        List<Map<String,String>> messages = List.of(
            Map.of("role","system","content", systemPrompt),
            Map.of("role","user","content", userPrompt)
        );

        String npcResponse = llmClient.requestChat(messages).trim();

        NpcMessage npcMsg = new NpcMessage();
        npcMsg.setRole("npc");
        npcMsg.setContent(npcResponse);
        npcMsg.setConversation(conv);
        messageRepo.save(npcMsg);

        return npcResponse;
    }

    // --- Métodos Privados para Construção de Prompts ---

    private String buildSystemPrompt(String npcName, String descricao) {
        return String.format(
            "Interprete um personagem de RPG chamado %s.\n" +
            "Descrição do personagem: %s.\n\n" +
            "Você deve responder **apenas** com a fala do NPC, sem nenhum prefixo (ex: não escreva “%s:” na frente) nem repetir o histórico.\n" +
            "Responda de forma breve, natural e coerente com seu personagem. " +
            "Nunca peça desculpas, nunca recuse a conversa, e não repita saudações.",
            npcName,
            descricao,
            npcName
        );
    }

    private String buildUserPrompt(String npcName, List<NpcMessage> history, String userContent) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Histórico da conversa:\n");

        List<NpcMessage> lastMessages = history.stream()
            .sorted(Comparator.comparing(NpcMessage::getTimestamp).reversed())
            .limit(9)
            .sorted(Comparator.comparing(NpcMessage::getTimestamp))
            .collect(Collectors.toList());

        for (NpcMessage msg : lastMessages) {
            String autor = msg.getRole().equals("user") ? "Jogador" : npcName;
            prompt.append(String.format("%s: %s\n", autor, msg.getContent()));
        }

        prompt.append("Jogador: ").append(userContent).append("\n");
        prompt.append(npcName).append(":");
        return prompt.toString();
    }
    
    // Métodos de Conversão e Busca (DTOs, etc.)

    public NpcConversationResponseDTO getConversation(Long conversationId) {
        NpcConversation conv = conversationRepo.findById(conversationId)
            .orElseThrow(() -> new IllegalArgumentException("Conversa não encontrada"));
        return toConversationDto(conv);
    }

    public void deleteConversation(Long conversationId) {
        conversationRepo.deleteById(conversationId);
    }

    private NpcMessageResponseDTO toMessageDto(NpcMessage msg) {
        NpcMessageResponseDTO dto = new NpcMessageResponseDTO();
        dto.setId(msg.getId());
        dto.setRole(msg.getRole());
        dto.setContent(msg.getContent());
        dto.setTimestamp(msg.getTimestamp());
        return dto;
    }

    private NpcConversationResponseDTO toConversationDto(NpcConversation conv) {
        NpcConversationResponseDTO dto = new NpcConversationResponseDTO();
        dto.setId(conv.getId());
        dto.setNpcName(conv.getNpcName());
        dto.setDescricao(conv.getDescricao());
        dto.setCreatedAt(conv.getCreatedAt());
        dto.setMessages(
            conv.getMessages().stream()
                .map(this::toMessageDto)
                .toList()
        );
        return dto;
    }
}
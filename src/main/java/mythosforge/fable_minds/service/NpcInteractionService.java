package mythosforge.fable_minds.service;

import mythosforge.fable_minds.llm.PromptBuilder;
import mythosforge.fable_minds.config.security.auhentication.dto.NpcConversationResponseDTO;
import mythosforge.fable_minds.config.security.auhentication.dto.NpcMessageResponseDTO;
import mythosforge.fable_minds.llm.LlmClientService;
import mythosforge.fable_minds.llm.ResponseParser;
import mythosforge.fable_minds.models.NpcConversation;
import mythosforge.fable_minds.models.NpcMessage;
import mythosforge.fable_minds.repository.NpcConversationRepository;
import mythosforge.fable_minds.repository.NpcMessageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

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

    public NpcConversationResponseDTO createConversation(String npcName, String descricao) {
        NpcConversation conv = new NpcConversation();
        conv.setNpcName(npcName);
        conv.setDescricao(descricao);
        conv = conversationRepo.save(conv);

        String systemPrompt = PromptBuilder.buildSystemPrompt(npcName, descricao);
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
 
        String systemPrompt = PromptBuilder.buildSystemPrompt(conv.getNpcName(), conv.getDescricao());
        String userPrompt   = PromptBuilder.buildUserPrompt(
            conv.getNpcName(), conv.getMessages(), userContent
        );

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

    public NpcConversationResponseDTO getConversation(Long conversationId) {
        NpcConversation conv = conversationRepo.findById(conversationId)
            .orElseThrow(() -> new IllegalArgumentException("Conversa não encontrada"));
        return toConversationDto(conv);
    }

    public void deleteConversation(Long conversationId) {
        conversationRepo.deleteById(conversationId);
    }
}

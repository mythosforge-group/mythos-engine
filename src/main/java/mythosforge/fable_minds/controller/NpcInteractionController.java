package mythosforge.fable_minds.controller;

import mythosforge.fable_minds.dto.NpcConversationRequestDTO;
import mythosforge.fable_minds.dto.NpcConversationResponseDTO;
import mythosforge.fable_minds.dto.NpcMessageRequestDTO;
import mythosforge.fable_minds.models.NpcConversation;
import mythosforge.fable_minds.service.NpcInteractionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/personagens/dnd/npc")
public class NpcInteractionController {

    private final NpcInteractionService npcService;

    public NpcInteractionController(NpcInteractionService npcService) {
        this.npcService = npcService;
    }

    @PostMapping("/conversas")
    public ResponseEntity<NpcConversationResponseDTO> novaConversa(
            @RequestBody NpcConversationRequestDTO request
    ) {
        var dto = npcService.createConversation(
            request.getNpcName(),
            request.getDescricao()
        );
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/conversas/{id}/mensagens")
    public ResponseEntity<String> enviarMensagem(
            @PathVariable("id") Long conversationId,
            @RequestBody NpcMessageRequestDTO request
    ) {
        String reply = npcService.sendUserMessage(conversationId, request.getContent());
        return ResponseEntity.ok(reply);
    }

    // to fix - error 403
    @GetMapping("/conversas/{id}")
    public ResponseEntity<NpcConversationResponseDTO> buscarConversa(
            @PathVariable("id") Long conversationId
    ) {
        var dto = npcService.getConversation(conversationId);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/conversas/{id}")
    public ResponseEntity<Void> excluirConversa(
            @PathVariable("id") Long conversationId
    ) {
        npcService.deleteConversation(conversationId);
        return ResponseEntity.noContent().build();
    }
}
package mythosforge.fable_minds.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import mythosforge.fable_minds.dto.CampaignDTO;
import mythosforge.fable_minds.dto.CampaignResponseDTO;
import mythosforge.fable_minds.models.Campaign;
import mythosforge.fable_minds.service.CampaignService;

/* ENDPOINTS
POST /api/campaigns – criar nova campanha
GET /api/campaigns – listar todas as campanhas
GET /api/campaigns/{id} – buscar campanha por ID
GET /api/campaigns/user/{userId} – listar campanhas de um usuário
PUT /api/campaigns/{id} – atualizar campanha
DELETE /api/campaigns/{id} – excluir campanha
*/

@RestController
@RequestMapping("/api/campaigns")
@Tag(
    name = "Campaign Controller", 
    description = "Gerenciamento de campanhas criadas por usuários."
)
@Slf4j
public class CampaignController {
    private final CampaignService campaignService;

    public CampaignController(CampaignService campaignService) {
        this.campaignService = campaignService;
    }

    @PostMapping
    public ResponseEntity<CampaignResponseDTO> create(@RequestBody Campaign campaign) {
        Campaign saved = campaignService.create(campaign);
        CampaignResponseDTO resp = new CampaignResponseDTO(
            saved.getId(),
            saved.getTitle(),
            saved.getDescription(),
            saved.getUser().getId(),
            saved.getSystem().getId()
        );
        return ResponseEntity.ok(resp);
    }

    @GetMapping
    public List<CampaignDTO> listAll() {
        return campaignService.findAll()
            .stream()
            .map(CampaignDTO::new)
            .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Campaign> getById(@PathVariable Long id) {
        return ResponseEntity.ok(campaignService.findById(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Campaign>> listByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(campaignService.findByUserId(userId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Campaign> update(@PathVariable Long id, @RequestBody Campaign campaign) {
        return ResponseEntity.ok(campaignService.update(id, campaign));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        campaignService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handleNotFound(EntityNotFoundException ex) {
        return ResponseEntity.status(404).body(ex.getMessage());
    }
}

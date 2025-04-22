package mythosforge.fable_minds.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import mythosforge.fable_minds.models.Campaign;
import mythosforge.fable_minds.models.Users;
import mythosforge.fable_minds.service.CampaignService;
import mythosforge.fable_minds.service.UserService;



@RestController
@RequestMapping("/api/campaigns")
@Tag(
    name = "Campaign Controller", 
    description = "Gerenciamento de campanhas criadas por usuários."
)
@Slf4j
public class CampaignController {
    private final CampaignService campaignService;
    private final UserService userService;

    public CampaignController(CampaignService campaignService, UserService userService) {
        this.campaignService = campaignService;
        this.userService = userService;
    }

    @Operation(
        summary = "Criar nova campanha",
        description = "Cria uma nova campanha associada a um usuário existente."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Campanha criada com sucesso",
            content = @Content(
                mediaType = "application/json", 
                schema = @Schema(implementation = Campaign.class)
            )
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Requisição inválida", 
            content = @Content
        )
    })
    @PostMapping
    public ResponseEntity<Campaign> createCampaign(@RequestBody Campaign campaign) {
        return ResponseEntity.ok(campaignService.createCampaign(campaign));
    }
    @Operation(
        summary = "Listar campanhas de um usuário",
        description = "Retorna todas as campanhas criadas por um usuário específico através de seu ID."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Campanhas retornadas com sucesso",
            content = @Content(
                mediaType = "application/json", 
                schema = @Schema(implementation = Campaign.class)
            )
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Usuário não encontrado", 
            content = @Content
        )
    })
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Campaign>> getUserCampaigns(@PathVariable Long userId) {
        return ResponseEntity.ok(campaignService.getCampaignsByUserId(userId));
    }
}

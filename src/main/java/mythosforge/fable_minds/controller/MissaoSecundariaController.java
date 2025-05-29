package mythosforge.fable_minds.controller;

import java.security.Principal;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.extern.slf4j.Slf4j;
import mythosforge.fable_minds.controller.dto.MissaoSecundariaRequestDTO;
import mythosforge.fable_minds.models.MissaoSecundaria;
import mythosforge.fable_minds.service.MissaoSecundariaService;


@RestController
@RequestMapping("/api/missao-secundaria")
@Slf4j
public class MissaoSecundariaController {
    private final MissaoSecundariaService missaoSecundariaService;

    public MissaoSecundariaController(MissaoSecundariaService missaoSecundariaService) {
        this.missaoSecundariaService = missaoSecundariaService;
    }

    /**
     * Gera uma missão secundária aleatória um personagem aleatório de uma campanha aleatória.
     * Se o usuário ainda não tiver campanhas e/ou as campanhas não tiverem personagens, lança uma exceção.
     * @return Uma MissaoSecundaria gerada aleatoriamente.
     */
    @PostMapping("gerar-aleatorio")
    public MissaoSecundaria gerarMissaoSecundariaAleatoria(Principal principal) {
        String username = principal.getName();
        return missaoSecundariaService.gerarMissaoSecundariaAleatoria(principal.getName());
    }

    @PostMapping("gerar")
    public MissaoSecundaria gerarMissaoSecundaria(@RequestParam Long characterId, Principal principal) {
        // log.warn("Character ID: {}", characterDto);
        return missaoSecundariaService.gerarMissaoSecundaria(characterId, principal.getName());
    }
}


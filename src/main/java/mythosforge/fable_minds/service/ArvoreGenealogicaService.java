package mythosforge.fable_minds.service;

import jakarta.persistence.EntityNotFoundException;
import mythosforge.fable_minds.exceptions.BusinessException;
import mythosforge.fable_minds.models.ArvoreGenealogica;
import mythosforge.fable_minds.repository.ArvoreGenealogicaRepository;
import mythosforge.fable_minds.service.interfaces.ArvoreGenealogicaInterface;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ArvoreGenealogicaService implements ArvoreGenealogicaInterface {

    private final ArvoreGenealogicaRepository repository;

    public ArvoreGenealogicaService(ArvoreGenealogicaRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void salvarEstruturaGenealogica(Long personagemId, String jsonEstrutura) {
        if (personagemId == null || jsonEstrutura == null || jsonEstrutura.isBlank()) {
            throw new BusinessException("Dados inválidos para salvar árvore genealógica.");
        }

        ArvoreGenealogica arvore = ArvoreGenealogica.builder()
                .personagemId(personagemId)
                .estruturaJson(jsonEstrutura)
                .build();

        repository.save(arvore);
    }

    public String buscarEstruturaPorPersonagem(Long personagemId) {
        return repository.findByPersonagemId(personagemId)
                .map(ArvoreGenealogica::getEstruturaJson)
                .orElseThrow(() -> new BusinessException("Estrutura genealógica não encontrada para o personagem com ID: " + personagemId));
    }

    public void salvarImagem(Long personagemId, byte[] imagemBytes) {
        ArvoreGenealogica arvore = repository.findByPersonagemId(personagemId)
                .orElseThrow(() -> new BusinessException("Árvore não encontrada"));

        arvore.setImagemArvore(imagemBytes);
        repository.save(arvore);
    }

}

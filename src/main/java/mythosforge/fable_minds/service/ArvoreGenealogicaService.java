package mythosforge.fable_minds.service;

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

        ArvoreGenealogica arvore = ArvoreGenealogica.builder()
                .personagemId(personagemId)
                .estruturaJson(jsonEstrutura)
                .build();

        repository.save(arvore);
    }

    public String buscarEstruturaPorPersonagem(Long personagemId) {
        return repository.findByPersonagemId(personagemId)
                .map(ArvoreGenealogica::getEstruturaJson)
                .orElse(null);
    }
}


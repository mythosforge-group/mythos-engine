package mythosforge.fable_minds.service.interfaces;

public interface ArvoreGenealogicaInterface {
    public void salvarEstruturaGenealogica(Long personagemId, String jsonEstrutura);
    public String buscarEstruturaPorPersonagem(Long personagemId);

}

package mythosforge.fable_minds.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Pessoa {
    private String nome;
    private String ocupacao;
    private String origem;
    private List<String> eventos;
    private List<Pessoa> pais;

    @JsonProperty("nome")
    public String getNome() {
        return nome;
    }

    @JsonProperty("nome")
    public void setNome(String nome) {
        this.nome = nome;
    }

    @JsonProperty("ocupacao")
    public String getOcupacao() {
        return ocupacao;
    }

    @JsonProperty("ocupacao")
    public void setOcupacao(String ocupacao) {
        this.ocupacao = ocupacao;
    }

    @JsonProperty("origem")
    public String getOrigem() {
        return origem;
    }

    @JsonProperty("origem")
    public void setOrigem(String origem) {
        this.origem = origem;
    }

    @JsonProperty("eventos")
    public List<String> getEventos() {
        return eventos;
    }

    @JsonProperty("eventos")
    public void setEventos(List<String> eventos) {
        this.eventos = eventos;
    }

    @JsonProperty("pais")
    public List<Pessoa> getPais() {
        return pais;
    }

    @JsonProperty("pais")
    public void setPais(List<Pessoa> pais) {
        this.pais = pais;
    }
}


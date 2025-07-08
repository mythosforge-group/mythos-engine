package mythosforge.lore_weaver.modules.dto;



import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SuggestedConnectionDTO {
    private String nome;
    private String tipo; // Ex: "PERSONAGEM", "LOCAL", "EVENTO"
    private String resumo;
    @JsonProperty("tipo_relacao") // Ex: "RESIDE_EM", "PARTICIPOU_EM"
    private String tipoRelacao;
}
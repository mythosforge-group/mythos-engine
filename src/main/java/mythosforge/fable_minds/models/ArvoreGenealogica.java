package mythosforge.fable_minds.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArvoreGenealogica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long personagemId;
    @Column(name = "imagem_arvore")
    private byte[] imagemArvore;

    @Column(name = "estrutura_json", columnDefinition = "TEXT")
    private String estruturaJson; // Aqui vai o JSON completo retornado pela IA
}


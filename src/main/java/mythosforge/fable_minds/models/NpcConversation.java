package mythosforge.fable_minds.models;

import java.time.LocalDateTime;
import java.util.ArrayList;

import jakarta.persistence.*;
import jakarta.persistence.Table;
import java.util.List;
import lombok.Data;

@Entity
@Table(name = "npcconversation")
@Data
public class NpcConversation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String npcName;

    private String descricao;

    @OneToMany(mappedBy = "conversation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NpcMessage> messages = new ArrayList<>();

    private LocalDateTime createdAt = LocalDateTime.now();
}

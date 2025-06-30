package mythosengine.services.dialogue;

// src/main/java/com/mythosengine/services/dialogue/DialogueService.java



import mythosengine.core.entity.Entity;
import mythosengine.core.persistence.PersistencePort;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class DialogueService {

    // --- Constantes para os tipos de relação, evitando "magic strings" ---
    public static final String REL_STARTS_WITH = "STARTS_WITH";
    public static final String REL_HAS_CHOICE = "HAS_CHOICE";
    public static final String REL_LEADS_TO = "LEADS_TO";
    public static final String REL_CONTINUES_TO = "CONTINUES_TO";

    private final PersistencePort persistencePort;

    // Injeção de Dependência da Camada de Persistência (Ponto Fixo)
    public DialogueService(PersistencePort persistencePort) {
        this.persistencePort = persistencePort;
        System.out.println("SERVICE: Serviço de Diálogo inicializado.");
    }

    /**
     * Cria a entidade raiz para uma nova árvore de diálogo.
     * @param treeName Um nome para identificar a conversa.
     * @return A entidade que representa a árvore de diálogo.
     */
    public Entity createDialogueTree(String treeName) {
        Entity tree = new Entity("DialogueTree");
        tree.addProperty("name", treeName);
        persistencePort.save(tree);
        System.out.println("DIALOGUE_SERVICE: Árvore de diálogo '" + treeName + "' criada com ID " + tree.getId());
        return tree;
    }

    /**
     * Cria um nó de fala de um NPC.
     * @param text O texto que o NPC fala.
     * @param speakerId (Opcional) O ID da entidade que está falando.
     * @return A entidade do nó de diálogo.
     */
    public Entity createDialogueNode(String text, UUID speakerId) {
        Entity node = new Entity("DialogueNode");
        node.addProperty("text", text);
        if (speakerId != null) {
            node.addProperty("speakerId", speakerId);
        }
        persistencePort.save(node);
        System.out.println("DIALOGUE_SERVICE: Nó de diálogo criado com ID " + node.getId());
        return node;
    }

    /**
     * Cria uma opção de escolha para o jogador.
     * @param text O texto que o jogador vê.
     * @return A entidade da escolha do jogador.
     */
    public Entity createPlayerChoice(String text) {
        Entity choice = new Entity("PlayerChoice");
        choice.addProperty("text", text);
        persistencePort.save(choice);
        System.out.println("DIALOGUE_SERVICE: Escolha de jogador criada com ID " + choice.getId());
        return choice;
    }

    /**
     * Liga um nó de diálogo a uma escolha de jogador.
     * @param sourceNodeId O ID do nó de fala do NPC.
     * @param choiceId O ID da escolha disponível para o jogador.
     */
    public void linkNodeToChoice(UUID sourceNodeId, UUID choiceId) {
        // Carrega a entidade do repositório para garantir que estamos trabalhando com a versão mais recente
        Entity sourceNode = persistencePort.findById(sourceNodeId)
                .orElseThrow(() -> new IllegalArgumentException("Nó de diálogo não encontrado: " + sourceNodeId));

        sourceNode.addRelationship(choiceId, REL_HAS_CHOICE);
        persistencePort.save(sourceNode); // Salva a entidade modificada
    }

    /**
     * Liga uma escolha de jogador a um nó de diálogo de resposta.
     * @param choiceId O ID da escolha do jogador.
     * @param targetNodeId O ID do nó de resposta do NPC.
     */
    public void linkChoiceToNode(UUID choiceId, UUID targetNodeId) {
        Entity choice = persistencePort.findById(choiceId)
                .orElseThrow(() -> new IllegalArgumentException("Escolha de jogador não encontrada: " + choiceId));

        choice.addRelationship(targetNodeId, REL_LEADS_TO);
        persistencePort.save(choice);
    }

    /**
     * Define o nó inicial de uma árvore de diálogo.
     * @param treeId O ID da árvore de diálogo.
     * @param firstNodeId O ID do primeiro nó de fala.
     */
    public void setTreeRoot(UUID treeId, UUID firstNodeId) {
        Entity tree = persistencePort.findById(treeId)
                .orElseThrow(() -> new IllegalArgumentException("Árvore de diálogo não encontrada: " + treeId));

        tree.addRelationship(firstNodeId, REL_STARTS_WITH);
        persistencePort.save(tree);
    }
}
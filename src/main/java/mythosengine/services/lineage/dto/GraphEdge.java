package mythosengine.services.lineage.dto;

/**
 * Representa uma aresta (conexão) direcionada entre dois nós.
 * @param fromId O ID do nó de origem.
 * @param toId O ID do nó de destino.
 * @param label O texto opcional a ser exibido na aresta (ex: tipo de relação).
 */
public record GraphEdge(String fromId, String toId, String label) {}
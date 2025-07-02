package mythosengine.services.lineage;



import mythosengine.core.entity.Entity;
import mythosengine.core.persistence.PersistencePort;
import mythosengine.services.lineage.dto.GraphData;
import mythosengine.services.lineage.dto.GraphEdge;
import mythosengine.services.lineage.dto.GraphNode;
import mythosengine.services.lineage.dto.TraversalDirection;
import mythosengine.services.storage.InMemoryStorageAdapter;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class LineageService {

    private final PersistencePort persistencePort;

    public LineageService(PersistencePort persistencePort) {
        this.persistencePort = persistencePort;
    }

    /**
     * Constrói uma representação de grafo a partir de uma entidade raiz, seguindo um tipo de relação específico.
     *
     * @param rootEntityId O ID da entidade para iniciar a travessia.
     * @param relationshipType O tipo de relação a ser seguida (ex: "É_PAI_DE").
     * @param direction A direção da travessia (UPSTREAM para ancestrais, DOWNSTREAM para descendentes).
     * @param maxDepth A profundidade máxima da recursão para evitar loops e controlar o tamanho.
     * @return Um objeto GraphData contendo os nós e arestas do grafo resultante.
     */
    public GraphData buildGraph(UUID rootEntityId, String relationshipType, TraversalDirection direction, int maxDepth) {
        Entity rootEntity = persistencePort.findById(rootEntityId)
                .orElseThrow(() -> new IllegalArgumentException("Entidade raiz não encontrada: " + rootEntityId));

        Set<GraphNode> nodes = new HashSet<>();
        Set<GraphEdge> edges = new HashSet<>();
        Set<UUID> visited = new HashSet<>(); // Essencial para evitar recursão infinita em grafos com ciclos.

        traverse(rootEntity, relationshipType, direction, maxDepth, 0, nodes, edges, visited);

        return new GraphData(nodes, edges);
    }

    /**
     * Método auxiliar recursivo para percorrer o grafo de entidades.
     */
    private void traverse(Entity currentEntity, String relationshipType, TraversalDirection direction,
                          int maxDepth, int currentDepth, Set<GraphNode> nodes, Set<GraphEdge> edges, Set<UUID> visited) {

        // --- Condições de Parada da Recursão ---
        if (currentDepth > maxDepth || visited.contains(currentEntity.getId())) {
            return;
        }

        // --- Processamento do Nó Atual ---
        visited.add(currentEntity.getId());
        nodes.add(createGraphNodeFromEntity(currentEntity));

        // --- Lógica de Travessia (Recursão) ---
        if (direction == TraversalDirection.DOWNSTREAM) {
            traverseDownstream(currentEntity, relationshipType, direction, maxDepth, currentDepth, nodes, edges, visited);
        } else { // UPSTREAM
            traverseUpstream(currentEntity, relationshipType, direction, maxDepth, currentDepth, nodes, edges, visited);
        }
    }

    private void traverseDownstream(Entity currentEntity, String relationshipType, TraversalDirection direction, int maxDepth, int currentDepth, Set<GraphNode> nodes, Set<GraphEdge> edges, Set<UUID> visited) {
        currentEntity.getRelationships().stream()
                .filter(rel -> rel.type().equals(relationshipType))
                .forEach(rel -> {
                    // Adiciona a aresta ao nosso grafo
                    edges.add(new GraphEdge(rel.sourceId().toString(), rel.targetId().toString(), rel.type()));

                    // Carrega a próxima entidade e continua a travessia
                    persistencePort.findById(rel.targetId()).ifPresent(nextEntity ->
                            traverse(nextEntity, relationshipType, direction, maxDepth, currentDepth + 1, nodes, edges, visited)
                    );
                });
    }

    /**
     * IMPORTANTE: A travessia UPSTREAM é inerentemente ineficiente com nosso modelo de persistência atual,
     * pois as relações são armazenadas apenas na entidade de origem. Um banco de dados de grafos (Neo4j)
     * ou uma tabela de junção em SQL resolveriam isso com uma única consulta.
     * Para este exemplo, com o InMemoryStorageAdapter, simulamos essa consulta varrendo todos os dados.
     * ISSO NÃO É RECOMENDADO PARA PRODUÇÃO COM BANCOS DE DADOS TRADICIONAIS SEM INDEXAÇÃO ADEQUADA.
     */
    private void traverseUpstream(Entity currentEntity, String relationshipType, TraversalDirection direction, int maxDepth, int currentDepth, Set<GraphNode> nodes, Set<GraphEdge> edges, Set<UUID> visited) {
        if (!(persistencePort instanceof InMemoryStorageAdapter adapter)) {
            // Lança uma exceção se não for possível realizar a busca reversa de forma "aceitável".
            throw new UnsupportedOperationException("A travessia UPSTREAM só é suportada pelo InMemoryStorageAdapter nesta implementação de demonstração.");
        }

        adapter.findAll().forEach(potentialSource -> {
            potentialSource.getRelationships().stream()
                    .filter(rel -> rel.type().equals(relationshipType) && rel.targetId().equals(currentEntity.getId()))
                    .findFirst()
                    .ifPresent(rel -> {
                        // Adiciona a aresta ao nosso grafo
                        edges.add(new GraphEdge(rel.sourceId().toString(), rel.targetId().toString(), rel.type()));

                        // Continua a travessia a partir da entidade de origem encontrada
                        traverse(potentialSource, relationshipType, direction, maxDepth, currentDepth + 1, nodes, edges, visited);
                    });
        });
    }

    /**
     * Cria um GraphNode a partir de uma Entidade, tentando encontrar uma propriedade de "nome"
     * para usar como rótulo.
     */
    private GraphNode createGraphNodeFromEntity(Entity entity) {
        String label = entity.getProperty("nome")
                .or(() -> entity.getProperty("name"))
                .or(() -> entity.getProperty("title"))
                .map(Object::toString)
                .orElse(entity.getArchetype() + ":" + entity.getId().toString().substring(0, 4)); // Fallback

        return new GraphNode(entity.getId().toString(), label, Collections.emptyMap());
    }
}

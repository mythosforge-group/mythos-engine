package mythosengine.spi.lineage;

import mythosengine.core.entity.Entity;
import mythosengine.core.persistence.PersistencePort;
import mythosengine.services.storage.InMemoryStorageAdapter;
import mythosengine.services.storage.JsonFileStorageAdapter;
import mythosengine.spi.lineage.dto.GraphData;
import mythosengine.spi.lineage.dto.GraphEdge;
import mythosengine.spi.lineage.dto.GraphNode;
import mythosengine.spi.lineage.dto.TraversalDirection;

import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
public class LineageService {

    private final PersistencePort persistencePort;

    public LineageService(PersistencePort persistencePort) {
        this.persistencePort = persistencePort;
    }

    public GraphData buildGraph(UUID rootEntityId, String relationshipType, TraversalDirection direction, int maxDepth) {
        Entity rootEntity = persistencePort.findById(rootEntityId)
                .orElseThrow(() -> new IllegalArgumentException("Entidade raiz não encontrada: " + rootEntityId));

        Set<GraphNode> nodes = new HashSet<>();
        Set<GraphEdge> edges = new HashSet<>();
        Set<UUID> visited = new HashSet<>();

        traverse(rootEntity, relationshipType, direction, maxDepth, 0, nodes, edges, visited);

        return new GraphData(nodes, edges);
    }

    private void traverse(Entity currentEntity, String relationshipType, TraversalDirection direction,
                          int maxDepth, int currentDepth, Set<GraphNode> nodes, Set<GraphEdge> edges, Set<UUID> visited) {

        if (currentDepth > maxDepth || visited.contains(currentEntity.getId())) {
            return;
        }

        visited.add(currentEntity.getId());
        nodes.add(createGraphNodeFromEntity(currentEntity));

        if (direction == TraversalDirection.DOWNSTREAM) {
            traverseDownstream(currentEntity, relationshipType, direction, maxDepth, currentDepth, nodes, edges, visited);
        } else {
            traverseUpstream(currentEntity, relationshipType, direction, maxDepth, currentDepth, nodes, edges, visited);
        }
    }

    private void traverseDownstream(Entity currentEntity, String relationshipType, TraversalDirection direction, int maxDepth, int currentDepth, Set<GraphNode> nodes, Set<GraphEdge> edges, Set<UUID> visited) {
        currentEntity.getRelationships().stream()
                .filter(rel -> relationshipType == null || rel.type().equals(relationshipType))
                .forEach(rel -> {
                    edges.add(new GraphEdge(rel.sourceId().toString(), rel.targetId().toString(), rel.type()));
                    persistencePort.findById(rel.targetId()).ifPresent(nextEntity ->
                            traverse(nextEntity, relationshipType, direction, maxDepth, currentDepth + 1, nodes, edges, visited)
                    );
                });
    }

    private void traverseUpstream(Entity currentEntity, String relationshipType, TraversalDirection direction, int maxDepth, int currentDepth, Set<GraphNode> nodes, Set<GraphEdge> edges, Set<UUID> visited) {

        Collection<Entity> allEntities;
        if (persistencePort instanceof InMemoryStorageAdapter adapter) {
            allEntities = adapter.findAll();
        } else if (persistencePort instanceof JsonFileStorageAdapter adapter) {
            allEntities = adapter.findAll();
        } else {
            throw new UnsupportedOperationException("A travessia UPSTREAM não é suportada pela implementação de persistência atual: " + persistencePort.getClass().getName());
        }

        allEntities.forEach(potentialSource -> {
            potentialSource.getRelationships().stream()
                    .filter(rel -> relationshipType == null || rel.type().equals(relationshipType))
                    .filter(rel -> rel.targetId().equals(currentEntity.getId()))
                    .findFirst()
                    .ifPresent(rel -> {
                        edges.add(new GraphEdge(rel.sourceId().toString(), rel.targetId().toString(), rel.type()));
                        traverse(potentialSource, relationshipType, direction, maxDepth, currentDepth + 1, nodes, edges, visited);
                    });
        });
    }

    private GraphNode createGraphNodeFromEntity(Entity entity) {
        String label = entity.getProperty("nome")
                .or(() -> entity.getProperty("name"))
                .or(() -> entity.getProperty("title"))
                .map(Object::toString)
                .orElse(entity.getArchetype() + ":" + entity.getId().toString().substring(0, 4));

        Map<String, String> properties = new HashMap<>();
        entity.getProperty("ocupacao").ifPresent(value -> properties.put("ocupacao", value.toString()));
        entity.getProperty("origem").ifPresent(value -> properties.put("origem", value.toString()));

        return new GraphNode(entity.getId().toString(), label, properties);
    }
}

package mythosforge.chronicle_architect.visualizer;

import mythosengine.spi.lineage.GraphVisualizer;
import mythosengine.spi.lineage.dto.GraphData;
import mythosengine.spi.lineage.dto.GraphEdge;
import mythosengine.spi.lineage.dto.GraphNode;
import mythosengine.spi.lineage.dto.VisualizationOptions;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.stream.Collectors;

@Primary
@Component
public class AsciiDependencyGraphVisualizer implements GraphVisualizer {

    @Override
    public boolean supportsFormat(String format) {
        return "txt".equalsIgnoreCase(format);
    }

    @Override
    public byte[] visualize(GraphData data, VisualizationOptions options) {
        if (data == null || data.nodes() == null || data.nodes().isEmpty()) {
            return "Grafo vazio.".getBytes(StandardCharsets.UTF_8);
        }

        Set<String> targetNodes = data.edges().stream()
                .map(GraphEdge::toId)
                .collect(Collectors.toSet());

        Set<GraphNode> rootNodes = data.nodes().stream()
                .filter(node -> !targetNodes.contains(node.id()))
                .collect(Collectors.toSet());

        String entityType;
        if (!rootNodes.isEmpty()) {
            // Pega o arquétipo do primeiro nó raiz encontrado
            GraphNode rootNode = rootNodes.iterator().next();
            entityType = mapArchetypeToPortuguese(rootNode.archetype());
        } else {
            entityType = "Entidade";
        }

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("=== Grafo de Dependências de %s ===\n\n", entityType));

        // Começa a impressão a partir dos nós raiz
        for (GraphNode rootNode : rootNodes) {
            printNode(rootNode.id(), data, "", true, sb);
        }

        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    /**
     * Mapeia o nome técnico do arquétipo para um nome amigável em português.
     */
    private String mapArchetypeToPortuguese(String archetype) {
        if (archetype == null) {
            return "Entidade";
        }
        return switch (archetype.toLowerCase()) {
            case "book" -> "Livro";
            case "chapter" -> "Capítulo";
            case "glossaryterm" -> "Termo do Glossário";
            case "rule" -> "Regra";
            case "rulecomponent" -> "Componente de Regra";
            case "skillcomponent" -> "Habilidade";
            
            default -> archetype;
        };
    }

    private void printNode(String nodeId, GraphData data, String prefix, boolean isTail, StringBuilder sb) {
        GraphNode currentNode = data.nodes().stream()
                .filter(n -> n.id().equals(nodeId))
                .findFirst().orElse(null);

        if (currentNode == null) return;

        sb.append(prefix).append(isTail ? "└── " : "├── ").append(currentNode.label()).append("\n");

        Set<GraphEdge> prerequisites = data.edges().stream()
                .filter(edge -> edge.fromId().equals(nodeId))
                .collect(Collectors.toSet());

        int count = 0;
        for (GraphEdge edge : prerequisites) {
            count++;
            printNode(edge.toId(), data, prefix + (isTail ? "    " : "│   "), count == prerequisites.size(), sb);
        }
    }
}
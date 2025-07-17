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

        StringBuilder sb = new StringBuilder();
        sb.append("=== Grafo de Dependências de Regras ===\n\n");

        // Encontra os nós que não são alvo de nenhuma aresta (os nós principais)
        Set<String> targetNodes = data.edges().stream()
                .map(GraphEdge::toId) // CORRIGIDO: de targetId para toId
                .collect(Collectors.toSet());

        Set<GraphNode> rootNodes = data.nodes().stream()
                .filter(node -> !targetNodes.contains(node.id()))
                .collect(Collectors.toSet());

        // Começa a impressão a partir dos nós raiz
        for (GraphNode rootNode : rootNodes) {
            printNode(rootNode.id(), data, "", true, sb);
        }

        sb.append("\n=====================================\n");
        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    /**
     * Método recursivo para imprimir a árvore de dependências.
     */
    private void printNode(String nodeId, GraphData data, String prefix, boolean isTail, StringBuilder sb) {
        GraphNode currentNode = data.nodes().stream()
                .filter(n -> n.id().equals(nodeId))
                .findFirst().orElse(null);

        if (currentNode == null) return;

        sb.append(prefix).append(isTail ? "└── " : "├── ").append(currentNode.label()).append("\n");

        // Encontra todas as arestas que saem deste nó (seus pré-requisitos)
        Set<GraphEdge> prerequisites = data.edges().stream()
                .filter(edge -> edge.fromId().equals(nodeId)) // CORRIGIDO: de sourceId() para fromId()
                .collect(Collectors.toSet());

        int count = 0;
        for (GraphEdge edge : prerequisites) {
            count++;
            // Continua a recursão para o pré-requisito
            printNode(edge.toId(), data, prefix + (isTail ? "    " : "│   "), count == prerequisites.size(), sb); // CORRIGIDO: de targetId() para toId()
        }
    }
}
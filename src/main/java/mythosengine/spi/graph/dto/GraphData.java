package mythosengine.spi.graph.dto;

import java.util.Set;

/**
 * Contém a estrutura completa de um grafo, composta por um conjunto de nós e arestas.
 * Este é o principal objeto de dados retornado pelo LineageService.
 * @param nodes O conjunto de todos os nós no grafo.
 * @param edges O conjunto de todas as arestas que conectam os nós.
 */
public record GraphData(Set<GraphNode> nodes, Set<GraphEdge> edges) {}
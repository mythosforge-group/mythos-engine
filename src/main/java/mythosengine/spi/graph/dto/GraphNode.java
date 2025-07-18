package mythosengine.spi.graph.dto;

import java.util.Map;

/**
 * Representa um nó único em um grafo. É uma representação genérica de uma Entidade.
 * @param id O identificador único do nó (geralmente o UUID da Entidade).
 * @param label O texto que será exibido no nó (ex: nome do personagem).
 * @param archetype O tipo da entidade original (ex: "SkillComponent", "Book").
 * @param properties Metadados adicionais para estilização ou informação (ex: cor, forma).
 */
public record GraphNode(String id, String label, String archetype, Map<String, String> properties) {}
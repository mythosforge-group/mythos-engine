package mythosengine.services.lineage.visualizer;




import java.util.Map;
import lombok.Builder;
import lombok.Getter;

/**
 * Contém opções de configuração para a geração de uma representação visual de um grafo.
 */
@Getter
@Builder
public class VisualizationOptions {
    /** O formato de saída desejado (ex: "png", "svg"). */
    private final String outputFormat;
    /** Atributos globais para o grafo (ex: "bgcolor", "rankdir"). */
    private final Map<String, String> graphAttributes;
    /** Atributos padrão para todos os nós (ex: "shape", "style"). */
    private final Map<String, String> defaultNodeAttributes;
    /** Atributos padrão para todas as arestas (ex: "color", "arrowhead"). */
    private final Map<String, String> defaultEdgeAttributes;
}

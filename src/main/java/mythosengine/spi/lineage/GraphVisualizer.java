package mythosengine.spi.lineage;
import mythosengine.spi.lineage.dto.GraphData;
import mythosengine.spi.lineage.dto.VisualizationOptions;
/**
 * Interface para componentes capazes de transformar uma estrutura de dados de grafo
 * em uma representação visual (ex: uma imagem).
 */
public interface GraphVisualizer {
    /**
     * Verifica se este visualizador suporta o formato de saída solicitado.
     * @param format O formato, ex: "png".
     * @return true se o formato for suportado.
     */
    boolean supportsFormat(String format);

    /**
     * Gera e retorna a representação visual do grafo.
     * @param data O grafo a ser visualizado.
     * @param options As opções de customização para a visualização.
     * @return Um array de bytes contendo os dados da imagem ou do arquivo gerado.
     * @throws Exception Se ocorrer um erro durante a renderização.
     */
    byte[] visualize(GraphData data, VisualizationOptions options) throws Exception;
}

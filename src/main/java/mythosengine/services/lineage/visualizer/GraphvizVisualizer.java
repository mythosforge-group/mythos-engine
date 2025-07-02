package mythosengine.services.lineage.visualizer;

import mythosengine.services.lineage.dto.GraphData;
import mythosengine.services.lineage.dto.GraphNode;
import guru.nidi.graphviz.attribute.Label;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.MutableGraph;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static guru.nidi.graphviz.model.Factory.mutGraph;
import static guru.nidi.graphviz.model.Factory.mutNode;

/**
 * Uma implementação da interface GraphVisualizer que usa a biblioteca graphviz-java
 * para renderizar grafos em imagens.
 */
@Component
public class GraphvizVisualizer implements GraphVisualizer {


    @Override
    public boolean supportsFormat(String format) {
        return "png".equalsIgnoreCase(format) || "svg".equalsIgnoreCase(format);
    }

    /**
     * Gera e retorna a representação visual do grafo como um array de bytes.
     * @param data O grafo a ser visualizado.
     * @param options As opções de customização para a visualização.
     * @return Um array de bytes contendo os dados da imagem gerada.
     * @throws IOException Se ocorrer um erro durante a renderização ou conversão da imagem.
     */
    @Override
    public byte[] visualize(GraphData data, VisualizationOptions options) throws IOException {
        // Cria um grafo mutável e direcionado
        MutableGraph g = mutGraph("LineageGraph").setDirected(true);

        // Aplica atributos globais ao grafo, se fornecidos
        if (options != null && options.getGraphAttributes() != null) {
            options.getGraphAttributes().forEach((key, value) -> g.graphAttrs().add(key, value));
        }

        // Adiciona todos os nós ao grafo, construindo um label HTML detalhado
        for (GraphNode node : data.nodes()) {
            StringBuilder htmlLabel = new StringBuilder();
            htmlLabel.append("<b>").append(escapeHtml(node.label())).append("</b>"); // Nome em negrito

            // Adiciona a ocupação se existir
            String ocupacao = node.properties().get("ocupacao");
            if (ocupacao != null && !ocupacao.isBlank()) {
                htmlLabel.append("<br/><i>Ocupação:</i> ").append(escapeHtml(ocupacao));
            }

            // Adiciona a origem se existir
            String origem = node.properties().get("origem");
            if (origem != null && !origem.isBlank()) {
                htmlLabel.append("<br/><i>Origem:</i> ").append(escapeHtml(origem));
            }

            // Adiciona o nó ao grafo com o label HTML formatado
            g.add(mutNode(node.id()).add(Label.html(htmlLabel.toString())));
        }

        // Adiciona todas as arestas, conectando os nós já existentes
        for (var edge : data.edges()) {
            g.add(mutNode(edge.fromId()).addLink(mutNode(edge.toId())));
        }

        // Determina o formato de saída com base nas opções
        Format graphvizFormat = "png".equalsIgnoreCase(options.getOutputFormat()) ? Format.PNG : Format.SVG;

        // Renderiza a imagem para um objeto BufferedImage em memória
        BufferedImage bufferedImage = Graphviz.fromGraph(g)
                .render(graphvizFormat)
                .toImage();

        // Converte o BufferedImage para um array de bytes
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(bufferedImage, options.getOutputFormat(), baos);
            return baos.toByteArray();
        }
    }


    private String escapeHtml(String text) {
        if (text == null) {
            return "";
        }
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}

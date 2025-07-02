

package mythosengine.services.lineage.visualizer;

import mythosengine.services.lineage.dto.GraphData;
import mythosengine.services.lineage.dto.GraphNode;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.MutableGraph;
import org.springframework.stereotype.Component;

// --- Imports Adicionais Necessários ---
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static guru.nidi.graphviz.model.Factory.mutGraph;
import static guru.nidi.graphviz.model.Factory.mutNode;

@Component
public class GraphvizVisualizer implements GraphVisualizer {

    @Override
    public boolean supportsFormat(String format) {
        return "png".equalsIgnoreCase(format) || "svg".equalsIgnoreCase(format);
    }

    @Override
    public byte[] visualize(GraphData data, VisualizationOptions options) throws IOException {
        MutableGraph g = mutGraph("LineageGraph").setDirected(true);

        if (options != null && options.getGraphAttributes() != null) {
            options.getGraphAttributes().forEach((key, value) -> g.graphAttrs().add(key, value));
        }

        for (GraphNode node : data.nodes()) {
            // Adicionando o label ao nó
            g.add(mutNode(node.id()).add("label", node.label()));
        }

        for (var edge : data.edges()) {
            g.add(mutNode(edge.fromId()).addLink(mutNode(edge.toId())));
        }

        Format graphvizFormat = "png".equalsIgnoreCase(options.getOutputFormat()) ? Format.PNG : Format.SVG;

        // --- INÍCIO DA CORREÇÃO ---

        // 1. Renderiza a imagem para um objeto BufferedImage em memória.
        BufferedImage bufferedImage = Graphviz.fromGraph(g)
                .render(graphvizFormat)
                .toImage();

        // 2. Cria um fluxo de saída que escreve em um array de bytes.
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        // 3. Usa a classe ImageIO do Java para escrever a imagem no fluxo de saída,
        //    especificando o formato (ex: "png").
        ImageIO.write(bufferedImage, options.getOutputFormat(), baos);

        // 4. Fecha o fluxo de saída.
        baos.close();

        // 5. Retorna o array de bytes contido no fluxo.
        return baos.toByteArray();

        // --- FIM DA CORREÇÃO ---
    }
}
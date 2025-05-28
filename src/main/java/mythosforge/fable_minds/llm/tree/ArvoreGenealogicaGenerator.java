package mythosforge.fable_minds.llm.tree;

import guru.nidi.graphviz.attribute.Label;
import guru.nidi.graphviz.engine.*;
import guru.nidi.graphviz.model.*;
import mythosforge.fable_minds.models.Pessoa;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.stream.Collectors;

import static guru.nidi.graphviz.model.Factory.*;

public class ArvoreGenealogicaGenerator {

    public static void gerarImagemArvore(Pessoa root, String outputPath) throws IOException {
        MutableGraph g = mutGraph("ArvoreGenealogica").setDirected(true);
        buildGraph(g, root, null);
        Graphviz.fromGraph(g)
                .render(Format.PNG)
                .toFile(new File(outputPath));
    }

    private static void buildGraph(MutableGraph g, Pessoa pessoa, String paiId) {
        String id = UUID.randomUUID().toString();

        Label label = Label.html(
                "<b>" + pessoa.getNome() + "</b><br/>" +
                        "<i>Ocupação:</i> " + pessoa.getOcupacao() + "<br/>" +
                        "<i>Origem:</i> " + pessoa.getOrigem() + "<br/>" +
                        (pessoa.getEventos() != null && !pessoa.getEventos().isEmpty()
                                ? "<i>Eventos:</i><br/>" + pessoa.getEventos().stream()
                                .map(e -> "&#8226; " + e)  // bullet point
                                .collect(Collectors.joining("<br/>"))
                                : "")
        );

        g.add(mutNode(id).add(label));

        if (paiId != null) {
            g.add(mutNode(paiId).addLink(mutNode(id)));
        }

        if (pessoa.getPais() != null) {
            for (Pessoa pai : pessoa.getPais()) {
                buildGraph(g, pai, id);
            }
        }
    }
}



package mythosengine.runner;

// runner/DemoRunner.java



import mythosengine.core.entity.Entity;
import mythosengine.core.event.EntityPropertyChangedEvent;
import mythosengine.core.persistence.PersistencePort;
import mythosengine.services.auth.AuthService;
import mythosengine.services.dialogue.DialogueService;
import mythosengine.services.storage.InMemoryStorageAdapter;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class DemoRunner implements CommandLineRunner {


    private final PersistencePort persistencePort;
    private final AuthService authService;
    private final DialogueService dialogueService;
    private final ApplicationEventPublisher eventPublisher;

    public DemoRunner(PersistencePort persistencePort,
                      AuthService authService,
                      DialogueService dialogueService,
                      ApplicationEventPublisher eventPublisher) {
        this.persistencePort = persistencePort;
        this.authService = authService;
        this.dialogueService = dialogueService;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("  INICIANDO TESTE COMPLETO DOS PONTOS FIXOS DO FRAMEWORK");
        System.out.println("=".repeat(50) + "\n");

        // === Teste 1: Kernel - Motor de Entidades e Persistência ===
        System.out.println("--- 1. TESTANDO KERNEL: ENTIDADE E PERSISTÊNCIA ---");
        // Criando entidades com propriedades
        System.out.println("-> Criando entidade 'Gandalf'...");
        Entity gandalf = new Entity("Mago");
        gandalf.addProperty("nome", "Gandalf, o Cinzento");
        gandalf.addProperty("nivel", 20);
        gandalf.addProperty("possui_cajado", true);

        System.out.println("-> Criando entidade 'Frodo'...");
        Entity frodo = new Entity("Hobbit");
        frodo.addProperty("nome", "Frodo Bolseiro");
        frodo.addProperty("nivel", 5);
        frodo.addProperty("possui_anel", true);

        // Testando a porta de persistência (save)
        persistencePort.save(gandalf);
        persistencePort.save(frodo);

        // Testando a porta de persistência (findById) para validar o save
        Entity gandalfRecuperado = persistencePort.findById(gandalf.getId())
                .orElseThrow(() -> new RuntimeException("Falha ao recuperar Gandalf!"));
        System.out.println("-> Entidade recuperada com sucesso: " + gandalfRecuperado.getProperty("nome").orElse("NOME NÃO ENCONTRADO"));

        // Testando o sistema de relações
        System.out.println("-> Criando relação entre entidades...");
        gandalfRecuperado.addRelationship(frodo.getId(), "É_MENTOR_DE");
        persistencePort.save(gandalfRecuperado); // Salva a entidade com a nova relação
        System.out.println("--------------------------------------------------\n");


        // === Teste 2: Serviços - Autenticação e Diálogo ===
        System.out.println("--- 2. TESTANDO SERVIÇOS DE FRAMEWORK ---");
        // Testando o AuthService
        System.out.println("-> Testando AuthService para registrar 'Frodo'...");
        authService.registerUser("frodo_bolseiro", "one_ring_to_rule_them_all");

        // Testando o DialogueService para construir uma conversa complexa
        System.out.println("-> Testando DialogueService para construir uma árvore de diálogo...");
        Entity elrond = new Entity("Elfo");
        elrond.addProperty("nome", "Elrond Meio-elfo");
        persistencePort.save(elrond);

        Entity conselho = dialogueService.createDialogueTree("O Conselho de Elrond");
        Entity node1 = dialogueService.createDialogueNode("O Anel deve ser destruído.", elrond.getId());
        Entity node2 = dialogueService.createDialogueNode("A jornada até a Montanha da Perdição é perigosa.", elrond.getId());
        Entity choice1 = dialogueService.createPlayerChoice("Mas como?");
        Entity choice2 = dialogueService.createPlayerChoice("Eu levarei o Anel.");

        dialogueService.setTreeRoot(conselho.getId(), node1.getId());
        dialogueService.linkNodeToChoice(node1.getId(), choice1.getId());
        dialogueService.linkChoiceToNode(choice1.getId(), node2.getId());
        dialogueService.linkNodeToChoice(node2.getId(), choice2.getId());
        System.out.println("-> Árvore de diálogo construída com sucesso.");
        System.out.println("--------------------------------------------------\n");





        // === Verificação Final: Estado do Banco de Dados em Memória ===
        System.out.println("--- VERIFICAÇÃO FINAL: ESTADO DO BANCO DE DADOS ---");
        // Usamos um cast pois sabemos que estamos usando a implementação InMemory para este teste
        if (persistencePort instanceof InMemoryStorageAdapter adapter) {
            adapter.findAll().forEach(entity -> {
                System.out.println("-> Encontrado no DB: " + entity);
                // Imprime as relações da entidade para uma verificação mais completa
                if (!entity.getRelationships().isEmpty()) {
                    System.out.println("   Relações: " + entity.getRelationships());
                }
            });
        }
        System.out.println("--------------------------------------------------\n");

        System.out.println("=".repeat(50));
        System.out.println("  TESTE COMPLETO DOS PONTOS FIXOS CONCLUÍDO COM SUCESSO!");
        System.out.println("=".repeat(50));
    }
}

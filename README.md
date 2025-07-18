# 🏛️ Mythos Engine & Aplicações

**Mythos Engine** é um `framework` modular e extensível para a criação de aplicações de suporte a RPGs, desenvolvido com Java + Spring Boot. Ele fornece uma espinha dorsal robusta para a geração de conteúdo dinâmico, gerenciamento de entidades de jogo e integração com IAs generativas.

Este repositório contém o núcleo do `framework` e três `aplicações` de exemplo construídas sobre ele, cada uma demonstrando uma instanciação diferente de seus pontos flexíveis:

1. **🐲 Fable Minds:** Uma plataforma para Mestres de Jogo criarem e gerenciarem campanhas, personagens e NPCs.
2. **📚 Lore Weaver:** Uma ferramenta para escritores e world-builders criarem e conectarem artigos de lore de forma colaborativa.
3. **📜 Chronicle Architect:** Uma plataforma para designers criarem e publicarem manuais de regras e livros de cenário interativos.

## 🚀 Funcionalidades por Aplicação

🐲 Fable Minds
- **Criação de Personagens:** Geração de histórias e árvores genealógicas com base em sistema, raça e classe.
- **Criação de NPCs Dinâmicos:** Criação rápida de NPCs para mestres de jogo.
- **Simulação de Diálogos:** Interação com NPCs via WebSocket.
- **Gerenciamento de Campanhas:** Organização de campanhas baseadas em sistemas populares (D&D, Tormenta, etc.).

📚 Lore Weaver
- **Criação de Lore:** Escrita de artigos sobre personagens, locais e eventos de um universo ficcional.
- **Expansão de Conteúdo por IA:** Expande automaticamente um artigo de lore com mais detalhes e profundidade.
- **Sugestão de Conexões:** A IA analisa um artigo e sugere novos artigos relacionados, criando links entre eles.
- **Visualização de Relações:** Gera grafos que mostram as conexões entre os elementos da lore.

📜 Chronicle Architect
- **Estruturação de Manuais:** Geração de sugestões de capítulos para livros de regras e cenários.
- **Geração de Glossários:** Análise de texto para extrair e definir termos-chave.
- **Criação de Regras e Habilidades:** Modela componentes de regras, como habilidades com pré-requisitos.
- **Visualização de Dependências:** Gera grafos em texto (ASCII) para mostrar as dependências entre regras.

## 🛠️ Tecnologias UtilizadasLinguagem: 
- Java 21+Framework: Spring Boot, Spring Security, Spring Data 
- JPABanco de Dados: PostgreSQL (produção/dev) e H2 (testes)
- ORM: Hibernate
- IA / LLM: Google Gemini API e suporte para modelos locais (via LM Studio)
- Outros: Lombok, PlantUML (para diagramas), Swagger (para documentação da API)

## 📁 Estrutura do Projeto:
Este é um monorepo que contém o framework e todas as suas aplicações. A estrutura principal é:
```
mythos-engine/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── mythosengine/            # 🧠 NÚCLEO DO FRAMEWORK
│   │   │   │   ├── core/
│   │   │   │   ├── security/
│   │   │   │   ├── services/
│   │   │   │   └── spi/                 # 🔌 PONTOS FLEXÍVEIS (Interfaces)
│   │   │   └── mythosforge/
│   │   │       ├── fable_minds/         # 🐲 APLICAÇÃO 1: Fable Minds
│   │   │       ├── lore_weaver/         # 📚 APLICAÇÃO 2: Lore Weaver
│   │   │       └── chronicle_architect/ # 📜 APLICAÇÃO 3: Chronicle Architect
│   │   └── resources/
│   │       ├── application.yaml         # Configurações globais (chaves de API, perfis)
│   │       └── templates/               # Templates de prompts
│   └── test/
└── pom.xml                              # Arquivo de build com as dependências
```


## 🧪 Executando Localmente1:

#### 1. Clone o repositório:
```bash
git clone https://github.com/seu-usuario/seu-repositorio.git
```
```bash
cd seu-repositorio
```
#### 2. Configure as Variáveis de Ambiente:
No arquivo `src/main/resources/application.yaml`, preencha as chaves de API e segredos necessários:
```yaml
  secret: "SUA_CHAVE_SECRETA_SUPER_FORTE_AQUI"
  expiration: 86400000

gemini:
  api-key: "SUA_CHAVE_API_DO_GEMINI_AQUI"
```

#### 3. Escolha Qual Aplicação Executar:
Este projeto contém 3 aplicações. Para escolher qual delas iniciar, edite o arquivo `pom.xml` e descomente a linha 
```xml
<start-class> desejada </start-class>
```
#### 4. Execute a Aplicação:
Use o Maven Wrapper para iniciar a aplicação selecionada:
```bash
./mvnw spring-boot:run
```
A aplicação estará disponível em http://localhost:8080.

🧩 Acessando o Banco de Dados H2 (Padrão) Por padrão, a aplicação usa um banco de dados em memória H2. Você pode acessá-lo no navegador quando a aplicação estiver rodando:
- URL: http://localhost:8080/h2-console
- JDBC URL: jdbc:h2:mem:testdb
- User Name: sa
- Password: (deixe em branco)

### 📚 Documentação da API (Swagger):
Com a aplicação rodando, a documentação de todos os endpoints está disponível via Swagger UI no seguinte endereço: http://localhost:8080/swagger-ui/index.html

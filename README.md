# 🧠 Fables Minds – API Backend

**Fables Minds** é uma plataforma web desenvolvida pelo grupo **Mythos Forge** para auxiliar jogadores e mestres de RPG na criação de histórias interativas, personagens únicos e campanhas estruturadas com o apoio de inteligência artificial.

Este repositório contém o backend da aplicação, desenvolvido com **Java + Spring Boot**, e responsável por gerenciar usuários, sessões, dados narrativos e integração com uma LLM (Large Language Model) para geração de conteúdo dinâmico.

---

## 🚀 Funcionalidades Principais

- ✍️ **Modo de Escrita Livre Assistida**
- 🧙 **Criação de NPCs Dinâmicos para Mestres de Jogo**
- 📜 **Geração de Missões e Eventos Secundários**
- 💬 **Simulação de Diálogos com NPCs (via WebSocket)**
- 🎲 **Criação de Campanhas baseadas em Sistemas RPG populares**
- 🧬 **Construção de Linhagem e Herança de Personagens**
- 🔐 **Login, autenticação e gerenciamento de usuário**
- 📤 **Compartilhamento de histórias finalizadas**

---

## 🛠️ Tecnologias Utilizadas

- **Linguagem:** Java 21+
- **Frameworks:** Spring Boot, Spring Security, Spring Data JPA
- **Banco de Dados:** PostgreSQL
- **ORM:** Hibernate
- **IA / LLM:** Deepseek API / Llama 3
- **Comunicação em tempo real:** WebSockets
- **Outros:** Lombok, Swagger (para documentação futura)

---

## 📁 Estrutura do Projeto
```
fables-minds-api/
├── src/
│   ├── main/
│   │   |── java/
│   │   |   └── mythosforge/
│   │   |       └── fablesminds/
│   │   |           ├── config/                      # Arquivos de configuração da aplicação (segurança, CORS, WebSocket, etc.)
│   │   |           ├── controller/                  # Camada responsável por expor os endpoints REST
│   │   |           ├── model/                       # Entidades JPA que representam as tabelas do banco de dados
│   │   |           ├── repository/                  # Interfaces que estendem JpaRepository para acesso ao banco
│   │   |           ├── service/                     # Camada de serviço com a lógica de negócio da aplicação
│   │   |           └── FablesMindsApplication.java  # Classe principal que inicia a aplicação Spring Boot
|   |   └── resources/                               # Pasta para arquivos que não são de código java. Em geral são arquivos de configurações
|   |       |── application.properties               # Arquivo de variáveis de ambiente do projeto 
│   └── test/                                        # Pacote de testes unitários e de integração
├── pom.xml                                          # Arquivo de build com as dependências do Maven
└── README.md                                        # Documentação do projeto
```
---

## 🧪 Executando Localmente

### Pré-requisitos

- Java 21+
- Maven 3.8+
- PostgreSQL

### Configuração do banco

Crie um banco no PostgreSQL e atualize o `application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/fables_db
spring.datasource.username=seu_usuario
spring.datasource.password=sua_senha
spring.jpa.hibernate.ddl-auto=update
```

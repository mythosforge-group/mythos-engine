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

### Pré-requisitos

- Java 21+
- Maven 3.8+
- PostgreSQL

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
|   |       |── application.yaml                     # Arquivo de variáveis de ambiente do projeto 
│   └── test/                                        # Pacote de testes unitários e de integração
├── pom.xml                                          # Arquivo de build com as dependências do Maven
└── README.md                                        # Documentação do projeto
```
---

## 🧪 Executando Localmente

**Clone o repositório:**
```bash
   git clone https://github.com/seu-usuario/fables-minds-api.git
   cd fables-minds-api
```
### Configure o banco de dados:

O projeto utiliza por padrão um banco H2, também tem uma configuração para usar um banco de desenvolvimento PostGreSQL hospedado no [neon.tech](neon.tech) e terá uma configuração para usar um banco de dados Postgre local.
 - Caso queira usar o banco de desenvolvimento hospedado na `neon`, use o perfil dev
 - Caso queira usar o banco H2 use o perfil test
 - Caso queria usar um banco local, use o perfil devlocal ⚠️(criar esse perfil)

Para executar o projeto no perfil que você quiser use o comando:

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=perfil-desejado
```
Ou mude o perfil ativo em [application.yaml](src\main\resources\application.yaml) para o perfil desejado e execute:
```bash
./mvnw spring-boot:run
```

Ou com sua IDE (IntelliJ, Eclipse, VSCode), executando a classe FablesMindsApplication.

### 🧩 Acessando o Banco de Dados H2 (in-memory)
O H2 Database é carregado na memória em tempo de execução. Caso você execute esse perfil você pode conectar usando um cliente de banco de dados SQL (como DBeaver ou MySQL Workbanch) e você pode ver as credenciais de acesso em [application-test.yaml](src\main\resources\application-test.yaml)
Você também pode acessar via navegador com o link [http://localhost:8080/h2-console](http://localhost:8080/h2-console) (quando a aplicação estiver rodando).

⚠️ Por padrão, o console H2 só estará acessível se spring.h2.console.enabled=true estiver no application-test.yaml.

### 🛠️ Como usar outro banco (PostgreSQL, etc.)
Para usar um banco PostgreSQL local:

Crie um banco:

```sql
CREATE DATABASE fables_db;
```
Atualize o arquivo application-devlocal.yaml:
```yaml
spring:
    datasource:
        url: jdbc:postgresql://localhost:5432/fables_db
        username: seu_usuario
        password: sua_senha
    jpa:
        hibernate:
            ddl-auto: update
```
Reinicie a aplicação. As tabelas serão criadas automaticamente.

📌 Observações
O banco H2 some ao desligar a aplicação. Use PostgreSQL para persistência real em ambiente de desenvolvimento.

##  📚 A documentação do Swagger
Para ver a documentação da API acesse [/swagger-ui/index.html]()

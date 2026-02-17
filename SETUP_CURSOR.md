# Configuração para rodar SW_TSE_Communication no Cursor

## Extensões necessárias

Instale as extensões recomendadas no Cursor:

1. **Extension Pack for Java** (`vscjava.vscode-java-pack`)
   - Inclui: Language Support for Java, Debugger for Java, Maven for Java, Test Runner
   - Necessário para compilar e executar o projeto

2. **Spring Boot Extension Pack** (`vmware.vscode-boot-dev-pack`)
   - Inclui: Spring Boot Tools, Spring Initializr, Spring Boot Dashboard
   - Facilita rodar e debugar aplicações Spring Boot

3. **Spring Boot Dashboard** (`vscjava.vscode-spring-boot-dashboard`)
   - Painel lateral com botão ▶️ para iniciar a API

### Como instalar

- Abra a paleta de comandos (`Ctrl+Shift+P` ou `Cmd+Shift+P`)
- Digite: `Extensions: Show Recommended Extensions`
- Clique em **Instalar** em cada extensão recomendada

Ou instale manualmente pela aba Extensions (Ctrl+Shift+X).

## Pré-requisitos

- **Java 17** (o projeto usa `java.version=17` no pom.xml)
- **Maven** (geralmente incluído nas extensões Java)
- **PostgreSQL** acessível (conforme `application-dev.properties`)

## Como rodar a API

### Opção 1: Spring Boot Dashboard (recomendado)

1. Instale as extensões acima
2. Aguarde o Cursor indexar o projeto (barra de progresso no canto inferior)
3. Na barra lateral esquerda, abra o **Spring Boot Dashboard**
4. Localize **SW_TSE_Communication**
5. Clique no ícone ▶️ (Run) ao lado do projeto

### Opção 2: Debug (F5)

1. Pressione **F5** ou vá em Run > Start Debugging
2. Selecione a configuração **SW_TSE_Communication (Spring Boot)**

### Opção 3: Terminal (Maven)

```bash
./mvnw spring-boot:run
```

Ou, se tiver Maven instalado globalmente:

```bash
mvn spring-boot:run
```

## Configuração

- O perfil ativo é **dev** (`spring.profiles.active=dev`)
- As configurações estão em `src/main/resources/application-dev.properties`
- Variáveis de ambiente sobrescrevem as propriedades (veja `env.example`)
- O arquivo `.env` existe para referência; o Spring Boot usa `application-dev.properties` por padrão

## Porta

A API sobe na porta **8082** por padrão. Após iniciar:

- API: http://localhost:8082
- Swagger UI: http://localhost:8082/swagger-ui

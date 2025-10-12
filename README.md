# SW_TSE_Communication

API de integração com o sistema TSE para gestão de pessoas, contratos e autenticação.

## 📋 Descrição

Esta aplicação Spring Boot funciona como uma camada de integração entre sistemas externos e o TSE (Sistema de Gestão Hoteleira), oferecendo funcionalidades de:

- ✅ Cadastro e gestão de pessoas
- ✅ Autenticação e geração de tokens
- ✅ Consulta de contratos de clientes
- ✅ Integração com APIs externas (Brasil API)
- ✅ Operação dual: API externa ou banco de dados direto

## 🚀 Tecnologias

- **Java 17**
- **Spring Boot 3.5.4**
- **Spring Data JPA**
- **PostgreSQL**
- **OpenFeign** (para integração com APIs externas)
- **Spring Boot Actuator** (para monitoramento)
- **Swagger/OpenAPI** (para documentação)
- **Lombok** (para redução de boilerplate)

## 🛠️ Configuração e Execução

### Pré-requisitos

- Java 17+
- Maven 3.6+
- PostgreSQL 12+
- Acesso ao sistema TSE

### 1. Clone o repositório

```bash
git clone <repository-url>
cd SW_TSE_Communication
```

### 2. Configure as variáveis de ambiente

Copie o arquivo de exemplo e configure suas credenciais:

```bash
cp env.example .env
```

Edite o arquivo `.env` com suas configurações:

```bash
# Configurações do Sistema TSE
API_TSE_URL=http://10.100.3.24:4003
API_TSE_USERNAME=seu_usuario
API_TSE_PASSWORD=sua_senha

# Configurações do Banco de Dados
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/tse_db
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=sua_senha_db
```

### 3. Execute a aplicação

#### Desenvolvimento (com logs detalhados e Swagger habilitado):
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

#### Produção:
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

### 4. Acesse a aplicação

- **API Base URL**: `http://localhost:8082`
- **Swagger UI**: `http://localhost:8082/swagger-ui.html`
- **Health Check**: `http://localhost:8082/actuator/health`
- **Métricas**: `http://localhost:8082/actuator/metrics`

## 📚 Endpoints Principais

### Pessoa
- `POST /api/v1/pessoa` - Cadastrar/atualizar pessoa
- `POST /api/v1/pessoa/json` - Converter DTO para formato API

### Autenticação
- `POST /api/v1/auth/logar` - Login de cliente

### Contratos
- `GET /api/v1/painelcliente/meuscontratos` - Listar contratos do cliente

### Lookup
- `GET /api/v1/lookup/TiposDocumentoPessoa` - Listar tipos de documento

## 🔧 Configurações

### Modos de Operação

A aplicação suporta dois modos de operação configurados via `database.enabled`:

- **`true`**: Opera diretamente com o banco PostgreSQL
- **`false`**: Opera via API externa do TSE

### Profiles Spring

- **`dev`**: Desenvolvimento (logs detalhados, Swagger habilitado)
- **`prod`**: Produção (logs otimizados, Swagger desabilitado)

## 🧪 Testes

Execute os testes unitários:

```bash
mvn test
```

Execute com relatório de cobertura:

```bash
mvn clean test jacoco:report
```

## 📊 Monitoramento

A aplicação expõe endpoints de monitoramento via Spring Boot Actuator:

- `/actuator/health` - Status da aplicação
- `/actuator/info` - Informações da aplicação
- `/actuator/metrics` - Métricas de performance
- `/actuator/prometheus` - Métricas no formato Prometheus

## 🔒 Segurança

> ⚠️ **Importante**: Esta aplicação está em desenvolvimento. A implementação de Spring Security está planejada para a fase final do projeto.

Atualmente, os endpoints estão abertos para desenvolvimento. Para produção, implemente:

- Autenticação JWT
- Autorização baseada em roles
- Rate limiting
- HTTPS obrigatório

## 🐛 Troubleshooting

### Problemas Comuns

1. **Erro de conexão com banco**:
   - Verifique se o PostgreSQL está rodando
   - Confirme as credenciais no arquivo `.env`

2. **Erro de conexão com TSE**:
   - Verifique se a URL do TSE está acessível
   - Confirme usuário e senha do TSE

3. **Porta já em uso**:
   - Mude a porta no `application.properties` ou variável `SERVER_PORT`

## 📝 Logs

Os logs são configurados por profile:

- **Dev**: Logs detalhados com nível DEBUG
- **Prod**: Logs otimizados com nível INFO/WARN

## 🤝 Contribuição

1. Faça um fork do projeto
2. Crie uma branch para sua feature (`git checkout -b feature/nova-feature`)
3. Commit suas mudanças (`git commit -am 'Adiciona nova feature'`)
4. Push para a branch (`git push origin feature/nova-feature`)
5. Abra um Pull Request

## 📄 Licença

Este projeto está sob a licença Apache 2.0. Veja o arquivo `LICENSE` para mais detalhes.

## 👥 Equipe

- **Desenvolvimento**: Equipe SW
- **Contato**: dev@sw.com.br

---

**Versão**: 1.0.0  
**Última atualização**: 2024

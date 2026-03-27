# Setup e Execucao Local - Backend

Este documento consolida o que foi implementado ate aqui e como executar sozinho no ambiente local.

## 1. O que foi implementado

### 1.1 Estrutura inicial do backend

Foi criada uma base com arquitetura hibrida:

- `services/auth-service`: autenticacao/autorizacao
- `services/core-service`: dominio principal (ainda em placeholder)
- `services/loyalty-service`: pontos/fidelizacao (ainda em placeholder)
- `platform/observability`: diretrizes de observabilidade

### 1.2 Infraestrutura local com Docker Compose

Arquivo: `docker-compose.yml`

Servicos provisionados:

- PostgreSQL 16 (`5432`)
- Redis 7 (`6379`)
- RabbitMQ 3.13 com management (`5672`, `15672`)

Todos com `healthcheck` configurado.

### 1.3 Bootstrap real do auth-service

Foi gerado e ajustado um projeto Spring Boot com:

- Java 21
- Spring Boot 3.3.6
- Security + OAuth2 Resource Server + Authorization Server
- JPA + Flyway + PostgreSQL
- Actuator

Arquivos principais:

- `services/auth-service/pom.xml`
- `services/auth-service/src/main/resources/application.yml`
- `services/auth-service/src/main/java/com/arenainteligente/auth/infrastructure/security/SecurityConfig.java`
- `services/auth-service/src/main/java/com/arenainteligente/auth/interfaces/rest/HealthProbeController.java`
- `services/auth-service/src/main/resources/db/migration/V1__init.sql`

### 1.4 Seguranca inicial (temporaria)

- `GET /actuator/health` e `GET /actuator/info` liberados.
- Demais rotas exigem autenticacao.
- `httpBasic` foi usado temporariamente para permitir bootstrap e validacao inicial.

### 1.5 Validacao ja realizada

- `./mvnw test` no `auth-service`: sucesso.
- Stack Docker local: sobe com sucesso.

## 2. Pre-requisitos

- Docker + Docker Compose funcionando
- Java 21 instalado e `JAVA_HOME` configurado

## 3. Passo a passo para executar sozinho

### Passo 1 - Subir infraestrutura

```bash
cd "/home/matheus/projetos/Arena Inteligente/backend"
docker compose up -d
docker compose ps
```

### Passo 2 - Validar Java

```bash
java -version
echo $JAVA_HOME
```

Se necessario, ajustar:

```bash
export JAVA_HOME="$(dirname "$(dirname "$(readlink -f "$(which javac)")")")"
export PATH="$JAVA_HOME/bin:$PATH"
```

### Passo 3 - Rodar testes do auth-service

```bash
cd "/home/matheus/projetos/Arena Inteligente/backend/services/auth-service"
./mvnw test
```

### Passo 4 - Subir auth-service

```bash
./mvnw spring-boot:run
```

### Passo 5 - Verificar health

```bash
curl http://localhost:8081/actuator/health
curl http://localhost:8081/actuator/info
```

## 4. Proximos passos recomendados

1. Implementar OAuth2/OIDC de fato no `auth-service`.
2. Modelar tenant, user, membership e RBAC (`owner`, `customer`).
3. Bootstrap do `core-service` no mesmo padrao tecnico.
4. Definir contratos entre `core-service` e `loyalty-service`.

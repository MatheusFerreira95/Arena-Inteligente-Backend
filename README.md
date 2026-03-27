# Backend - Arena Inteligente

Repositório backend com arquitetura híbrida:

- `services/auth-service`: identidade e acesso (OAuth2/OIDC)
- `services/core-service`: domínios centrais (quadras, reservas, billing, operações, reporting)
- `services/loyalty-service`: pontuação e fidelização

## Objetivo desta fase

Base de infraestrutura para estudo e evolução:

- Docker local (PostgreSQL + Redis + RabbitMQ)
- CI inicial (GitHub Actions)
- Observabilidade mínima (logs estruturados + OpenTelemetry preparado)

## Estrutura inicial

```text
backend/
  services/
    auth-service/
    core-service/
    loyalty-service/
  platform/observability/
  .github/workflows/
  docker-compose.yml
```

## Próximos passos

1. Bootstrap dos projetos Spring Boot por serviço
2. Configuração de segurança no `auth-service`
3. Contratos OpenAPI iniciais
4. Testes base com Testcontainers

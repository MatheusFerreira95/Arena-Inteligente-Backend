# Observabilidade (base)

Padrão inicial:

- logs estruturados JSON com `trace_id`, `tenant_id`, `user_id`
- OpenTelemetry preparado para tracing
- backend de logs padrão: Loki/Grafana (Splunk opcional via exportador/HEC)

## Diretrizes

1. Não acoplar domínio à ferramenta de observabilidade
2. Padronizar correlação por `trace_id`
3. Auditar eventos críticos: auth, reservas, pagamentos, pontos

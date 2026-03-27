# core-service

Serviço principal de negócio.

Escopo inicial:

- gestão de quadras e disponibilidade
- reservas avulsas e recorrência semanal
- financeiro manual (preparado para gateway)
- operações e reporting básico
- integração com loyalty por contrato

Base técnica atual:

- Spring Boot 3.3.6
- Security como Resource Server (JWT)
- JPA + Flyway + PostgreSQL
- Actuator habilitado

Primeiro slice funcional entregue:

- CRUD basico de quadras
- disponibilidade semanal por quadra
- reserva avulsa com validacao de conflito de horario

Segundo slice funcional entregue:

- bloqueios de agenda/manutencao por periodo
- validacao de disponibilidade semanal na criacao da reserva
- validacao de bloqueio de quadra na criacao da reserva
- consulta de agenda por periodo (`/api/v1/reservations/agenda`)

Observacao de tenancy:

- endpoints funcionais exigem header `X-Tenant-Id`

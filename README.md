# Cofrinhos API

API de controle de economias gamificado. Você cria **cofrinhos** (metas de poupança), registra **aportes** e ganha **XP**, **níveis**, **sequências semanais** (streaks), **conquistas** e posição em um **ranking** atualizado ao vivo.

Projeto de portfólio, pensado para exercitar arquitetura, mensageria, cache, segurança e observabilidade em um sistema pequeno, mas completo, com front-end incluído.

## Principais recursos

- Cofrinhos por usuário, com categoria (renda fixa, renda variável, reserva de emergência, economia), valor da meta e progresso.
- Aportes que atualizam o cofrinho e concluem a meta quando ela é atingida.
- Gamificação processada de forma assíncrona: cada aporte gera XP, atualiza o nível e a sequência semanal e pode desbloquear conquistas.
- Ranking de XP em Redis (sorted set), com stream em tempo real (SSE) e reconstrução a partir do banco.
- Dashboard que reúne perfil, conquistas e posição do usuário em uma única chamada.
- Autenticação OAuth2/OIDC com Keycloak; cada usuário só enxerga os próprios cofrinhos.
- Front-end em HTML, CSS e JavaScript puros, servido pelo próprio Spring.

## Tecnologias

| Área | Escolha |
| --- | --- |
| Linguagem e framework | Java 21, Spring Boot 4.1.1 (Spring Framework 7) |
| Persistência | PostgreSQL 16, Spring Data JPA, Flyway |
| Mensageria | Apache Kafka 3.8 (modo KRaft) |
| Cache e ranking | Redis 7 |
| Segurança | Spring Security OAuth2 Resource Server, Keycloak 26 |
| Reativo na borda | Spring WebFlux (dashboard e SSE do ranking) |
| Observabilidade | Micrometer, Prometheus, Grafana, OpenTelemetry, Jaeger |
| Testes | JUnit 5, Mockito, Testcontainers |
| CI | GitHub Actions |

## Arquitetura

O projeto segue **arquitetura hexagonal** com conceitos de **DDD**:

- **Domínio**: entidades e regras de negócio (cofrinho, aporte, perfil de XP, conquistas), sem dependência de framework.
- **Aplicação**: casos de uso (criar cofrinho, registrar aporte, listar cofrinhos, consultar dashboard) e as portas, que são as interfaces que o domínio exige do mundo externo.
- **Adaptadores de entrada**: controllers REST e o consumidor Kafka.
- **Adaptadores de saída**: persistência JPA, publicação de eventos no Kafka e Redis.

### Fluxo de um aporte

1. `POST /cofrinhos/{cofrinhoId}/aportes` registra o aporte e atualiza o cofrinho na mesma transação.
2. O evento `AporteRegistradoEvent` é publicado no tópico Kafka `aporte-registrado`.
3. O consumidor (grupo `cofrinhos-api`) calcula o XP, atualiza nível e sequência, desbloqueia conquistas e atualiza o ranking no Redis.
4. O front-end consulta o dashboard por alguns segundos e mostra os toasts de XP e de conquista. Quem está na aba Ranking recebe a atualização pelo stream SSE.

Como o XP é processado por evento, a resposta do aporte não espera a gamificação (consistência eventual).

### Regras de gamificação

- XP por aporte: `10 + min(floor(valor / 10), 50) + semanaDeSequência × 5`.
- Nível: `xpTotal / 500 + 1`.
- Sequência: contada em **semanas** seguidas com pelo menos um aporte.
- Conquistas: *Primeiro cofrinho concluído*, *Reserva de emergência completa* e *Streak de 4 semanas*.

## Endpoints

Todos exigem token JWT, exceto os de `/actuator` listados abaixo.

| Método | Rota | Descrição |
| --- | --- | --- |
| `POST` | `/cofrinhos` | Cria um cofrinho para o usuário autenticado |
| `GET` | `/cofrinhos` | Lista os cofrinhos do usuário autenticado |
| `GET` | `/cofrinhos/{id}` | Busca um cofrinho (404 se não for do usuário) |
| `POST` | `/cofrinhos/{cofrinhoId}/aportes` | Registra um aporte |
| `GET` | `/dashboard` | Perfil de XP, conquistas e posição no ranking |
| `GET` | `/perfis-xp/{usuarioId}` | Perfil de XP de um usuário |
| `GET` | `/ranking?limite=N` | Ranking de XP (lista) |
| `GET` | `/ranking/stream?limite=N` | Ranking ao vivo via Server-Sent Events |
| `GET` | `/actuator/health`, `/actuator/info`, `/actuator/prometheus` | Públicos |

Cofrinhos de outros usuários respondem **404** (e não 403), para não revelar a existência do recurso. Erros seguem o formato `ProblemDetail`.

## Como rodar

Pré-requisitos: **JDK 21** e **Docker** (com Docker Compose). O Maven Wrapper já está no projeto.

1. Entre na pasta do projeto:

```
   cd cofrinhos-api
```

2. Suba a infraestrutura (Postgres, Kafka, Redis, Keycloak, Prometheus, Grafana e Jaeger):

```
   docker compose up -d
```

3. Aguarde o Keycloak importar o realm. Esta chamada deve devolver `200`:

```
   curl http://localhost:8081/realms/cofrinhos
```

4. Suba a API (no PowerShell use `.\mvnw.cmd`; no Linux ou macOS, `./mvnw`):

```
   .\mvnw.cmd spring-boot:run
```

5. Abra `http://localhost:8080` e entre com o usuário de desenvolvimento `enzo` (senha `enzo123`).

As migrations do Flyway (`V1__create_tables.sql` e `V2__create_conquistas_table.sql`) rodam sozinhas na inicialização.

### Endereços úteis

| Serviço | URL |
| --- | --- |
| Aplicação e front-end | http://localhost:8080 |
| Keycloak (admin: `admin` / `admin`) | http://localhost:8081 |
| Prometheus | http://localhost:9090 |
| Grafana (`admin` / `admin`) | http://localhost:3000 |
| Jaeger | http://localhost:16686 |

> **Atenção:** todas as credenciais acima são **somente para desenvolvimento local**. O Keycloak roda em modo `start-dev` e o realm foi exportado sem as chaves privadas (o Keycloak gera novas ao importar). Para qualquer ambiente real, use segredos vindos de variáveis de ambiente e uma configuração de produção.

## Autenticação

O front-end usa o fluxo **Authorization Code com PKCE** no client público `cofrinhos-web` do realm `cofrinhos`. A API valida o JWT como *resource server*: o `issuer` é `http://localhost:8081/realms/cofrinhos`, a *audience* esperada é `cofrinhos-api` e o `sub` do token identifica o dono dos recursos. O *Direct access grants* do client está desativado.

Como o `EventSource` do navegador não envia o cabeçalho `Authorization`, o front-end lê o stream do ranking com `fetch` e interpreta o SSE manualmente.

## Observabilidade

- **Métricas**: Micrometer expõe `/actuator/prometheus`, e o Prometheus coleta a API em `host.docker.internal:8080`. O Grafana sobe com dashboards provisionados a partir de `observability/grafana`.
- **Traces**: OpenTelemetry envia os spans da API, do Kafka e do Redis ao Jaeger via OTLP (`http://localhost:4318/v1/traces`), com amostragem de 100%.

## Testes

```
.\mvnw.cmd test
```

A suíte combina testes unitários de domínio e casos de uso (JUnit 5 e Mockito) com testes de integração que usam **Testcontainers**, portanto o Docker precisa estar em execução.

## Integração contínua

O workflow em `.github/workflows` roda a cada push em `main` ou `master` e em pull requests: configura o JDK 21 (Temurin) com cache do Maven e executa `./mvnw -B test` no GitHub Actions.

## Estrutura do repositório

```
.
├── .github/workflows        # pipeline de CI
└── cofrinhos-api
    ├── docker-compose.yml   # infraestrutura local
    ├── keycloak             # realm exportado (importado automaticamente)
    ├── observability        # configuração do Prometheus e do Grafana
    └── src
        ├── main/java        # domínio, aplicação e adaptadores
        ├── main/resources   # application.yml, migrations Flyway e front-end (static)
        └── test             # testes unitários e de integração
```

## Próximos passos

- Ler credenciais de variáveis de ambiente (hoje os padrões de desenvolvimento estão no `application.yml` e no Compose).
- Empacotar a API em imagem Docker e incluí-la no Compose.
- Perfil de produção para o Keycloak (modo `start` com TLS e banco externo).

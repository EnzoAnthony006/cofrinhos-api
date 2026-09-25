CREATE TABLE cofrinhos (
                           id               UUID PRIMARY KEY,
                           usuario_id       UUID NOT NULL,
                           nome             VARCHAR(255) NOT NULL,
                           descricao        VARCHAR(255),
                           categoria        VARCHAR(255) NOT NULL,
                           valor_meta       NUMERIC(19,2) NOT NULL,
                           valor_acumulado  NUMERIC(19,2) NOT NULL,
                           status           VARCHAR(255) NOT NULL,
                           CONSTRAINT cofrinhos_categoria_check CHECK (categoria IN ('RENDA_FIXA', 'RENDA_VARIAVEL', 'RESERVA_EMERGENCIA', 'ECONOMIA')),
                           CONSTRAINT cofrinhos_status_check CHECK (status IN ('ATIVO', 'CONCLUIDO', 'ARQUIVADO'))
);

CREATE TABLE aportes (
                         id             UUID PRIMARY KEY,
                         cofrinho_id    UUID NOT NULL,
                         valor          NUMERIC(38,2) NOT NULL,
                         data_registro  TIMESTAMP(6) NOT NULL
);

CREATE TABLE perfis_xp (
                           usuario_id             UUID PRIMARY KEY,
                           xp_total               INTEGER NOT NULL,
                           sequencia_atual        INTEGER,
                           melhor_sequencia       INTEGER,
                           data_ultima_atividade  DATE
);
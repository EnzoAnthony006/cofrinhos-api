CREATE TABLE conquistas_desbloqueadas (
                                          id                UUID PRIMARY KEY,
                                          usuario_id        UUID NOT NULL,
                                          definicao         VARCHAR(255) NOT NULL,
                                          data_desbloqueio  TIMESTAMP(6) NOT NULL,
                                          CONSTRAINT uk_conquistas_usuario_definicao UNIQUE (usuario_id, definicao)
);
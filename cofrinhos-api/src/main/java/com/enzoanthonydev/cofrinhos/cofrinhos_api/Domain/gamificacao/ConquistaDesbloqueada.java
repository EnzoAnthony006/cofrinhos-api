package com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.gamificacao;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class ConquistaDesbloqueada {

    private final UUID id;
    private final UUID usuarioId;
    private final DefinicaoConquista definicao;
    private final LocalDateTime dataDesbloqueio;

    private ConquistaDesbloqueada (UUID id, UUID usuarioId, DefinicaoConquista definicao,
                                   LocalDateTime dataDesbloqueio) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.definicao = definicao;
        this.dataDesbloqueio = dataDesbloqueio;

    }
    public UUID getId() {
        return id;
    }
    public UUID getUsuarioId() {
        return usuarioId;
    }
    public DefinicaoConquista getDefinicao() {
        return definicao;
    }
    public LocalDateTime getDataDesbloqueio() {
        return dataDesbloqueio;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ConquistaDesbloqueada outra)) return false;
        return this.id.equals(outra.id);
    }
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

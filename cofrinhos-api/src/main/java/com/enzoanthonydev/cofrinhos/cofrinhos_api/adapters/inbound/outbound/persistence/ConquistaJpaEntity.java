package com.enzoanthonydev.cofrinhos.cofrinhos_api.adapters.inbound.outbound.persistence;

import com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.gamificacao.DefinicaoConquista;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "conquistas_desbloqueadas")
public class ConquistaJpaEntity {

    @Id
    private UUID id;

    @Column(name = "usuario_id", nullable = false)
    private UUID usuarioId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DefinicaoConquista definicaoConquista;

    @Column(name = "data_desbloqueio", nullable = false )
    private LocalDateTime dataDesbloqueio;

    protected ConquistaJpaEntity() {

    }
    public ConquistaJpaEntity(UUID id, UUID usuarioId, DefinicaoConquista definicao, LocalDateTime dataDesbloqueio) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.definicaoConquista = definicao;
        this.dataDesbloqueio = dataDesbloqueio;
    }
    public UUID getId() {
        return id;
    }
    public UUID getUsuarioId() {
        return usuarioId;
    }
    public DefinicaoConquista getDefinicao() {
        return definicaoConquista;
    }
    public LocalDateTime getDataDesbloqueio() {
        return dataDesbloqueio;
    }
}

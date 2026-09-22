package com.enzoanthonydev.cofrinhos.cofrinhos_api.adapters.inbound.outbound.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "perfis_xp")
public class PerfilXPJpaEntity {

    @Id
    private UUID usuarioId;

    private int xpTotal;

    @Column(name = "sequencia_atual")
    private int sequenciaAtual;

    @Column(name = "melhor_sequencia")
    private int melhorSequencia;

    @Column(name = "data_ultima_atividade")
    private LocalDate dataUltimaAtividade;

    protected PerfilXPJpaEntity() {

    }
    public PerfilXPJpaEntity(UUID usuarioId, int xpTotal, int sequenciaAtual, int melhorSequencia, LocalDate dataUltimaAtividade) {
        this.usuarioId = usuarioId;
        this.xpTotal = xpTotal;
        this.sequenciaAtual = sequenciaAtual;
        this.melhorSequencia = melhorSequencia;
        this.dataUltimaAtividade = dataUltimaAtividade;

    }
    public UUID getUsuarioId() {
        return usuarioId;
    }
    public int getXpTotal() {
        return xpTotal;
    }
    public int getSequenciaAtual() {
        return sequenciaAtual;
    }
    public int getMelhorSequencia() {
        return melhorSequencia;
    }
    public LocalDate getDataUltimaAtividade() {
        return dataUltimaAtividade;
    }
}

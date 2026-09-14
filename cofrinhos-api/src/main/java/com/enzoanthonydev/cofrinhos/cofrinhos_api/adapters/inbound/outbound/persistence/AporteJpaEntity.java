package com.enzoanthonydev.cofrinhos.cofrinhos_api.adapters.inbound.outbound.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "aportes")
public class AporteJpaEntity {

    @Id
    private UUID id;

    @Column(name = "cofrinho_id", nullable = false)
    private UUID cofrinhoId;

    @Column(nullable = false)
    private BigDecimal valor;

    @Column(name = "data_registro", nullable = false)
    private LocalDateTime dataRegistro;

    protected AporteJpaEntity() {

    }

    public AporteJpaEntity(UUID id, UUID cofrinhoId, BigDecimal valor, LocalDateTime dataRegistro) {
        this.id = id;
        this.cofrinhoId = cofrinhoId;
        this.valor = valor;
        this.dataRegistro = dataRegistro;
    }

    public UUID getId() {
        return id;
    }

    public UUID getCofrinhoId() {
        return cofrinhoId;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public LocalDateTime getDataRegistro() {
        return dataRegistro;
    }
}

package com.enzoanthonydev.cofrinhos.cofrinhos_api.adapters.inbound.outbound.persistence;

import com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.CategoriaInvestimento;
import com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.StatusCofrinho;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "cofrinhos")
public class CofrinhoJpaEntity {

    @Id
    private UUID id;

    @Column(name = "usuario_id", nullable = false)
    private UUID usuarioId;

    @Column(nullable = false)
    private String nome;

    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategoriaInvestimento categoria;

    @Column(name = "valor_meta", nullable = false, precision = 19, scale = 2)
    private BigDecimal valorMeta;

    @Column(name = "valor_acumulado", nullable = false, precision = 19, scale = 2)
    private BigDecimal valorAcumulado;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusCofrinho status;

    protected CofrinhoJpaEntity() {
        // construtor exigido pelo JPA/Hibernate, não use diretamente
    }

    public CofrinhoJpaEntity(UUID id, UUID usuarioId, String nome, String descricao,
                             CategoriaInvestimento categoria, BigDecimal valorMeta,
                             BigDecimal valorAcumulado, StatusCofrinho status) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.nome = nome;
        this.descricao = descricao;
        this.categoria = categoria;
        this.valorMeta = valorMeta;
        this.valorAcumulado = valorAcumulado;
        this.status = status;
    }

    public UUID getId() { return id; }
    public UUID getUsuarioId() { return usuarioId; }
    public String getNome() { return nome; }
    public String getDescricao() { return descricao; }
    public CategoriaInvestimento getCategoria() { return categoria; }
    public BigDecimal getValorMeta() { return valorMeta; }
    public BigDecimal getValorAcumulado() { return valorAcumulado; }
    public StatusCofrinho getStatus() { return status; }
}

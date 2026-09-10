package com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import java.util.UUID;

public class Cofrinho{

    private final UUID id;
    private final UUID usuarioId;
    private String nome;
    private String descricao;
    private final CategoriaInvestimento categoria;
    private final Dinheiro valorMeta;
    private Dinheiro valorAcumulado;
    private StatusCofrinho status;

    private Cofrinho(UUID id, UUID usuarioId, String nome, String descricao,
                     CategoriaInvestimento categoria, Dinheiro valorMeta,
                     Dinheiro valorAcumulado, StatusCofrinho status) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome do cofrinho não pode ser vazio");
        }
        if (categoria == null) {
            throw new IllegalArgumentException("Categoria é obrigatória");
        }
        if (valorMeta == null || valorMeta.valor().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor da meta deve ser maior que zero");
        }
        this.id = id;
        this.usuarioId = usuarioId;
        this.nome = nome;
        this.descricao = descricao;
        this.categoria = categoria;
        this.valorMeta = valorMeta;
        this.valorAcumulado = valorAcumulado;
        this.status = status;
    }

    public static Cofrinho criar(UUID usuarioId, String nome, String descricao,
                                 CategoriaInvestimento categoria, Dinheiro valorMeta) {
        return new Cofrinho(UUID.randomUUID(), usuarioId, nome, descricao,
                categoria, valorMeta, Dinheiro.zero(), StatusCofrinho.ATIVO);
    }
    public void arquivar() {
        this.status = StatusCofrinho.ARQUIVADO;
    }

    public void registrarAporte(Dinheiro valor) {
        if (status == StatusCofrinho.ARQUIVADO) {
            throw new IllegalStateException("Não é possível registrar aporte em um cofrinho arquivado");
        }
        this.valorAcumulado = this.valorAcumulado.somar(valor);
        if (this.valorAcumulado.maiorOuIgualA(this.valorMeta)) {
            this.status = StatusCofrinho.CONCLUIDO;
        }
    }

    public double progresso() {
        return valorAcumulado.valor()
                .divide(valorMeta.valor(), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .doubleValue();
    }

    public UUID getId() { return id; }
    public UUID getUsuarioId() { return usuarioId; }
    public String getNome() { return nome; }
    public String getDescricao() { return descricao; }
    public CategoriaInvestimento getCategoria() { return categoria; }
    public Dinheiro getValorMeta() { return valorMeta; }
    public Dinheiro getValorAcumulado() { return valorAcumulado; }
    public StatusCofrinho getStatus() { return status; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cofrinho outro)) return false;
        return this.id.equals(outro.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

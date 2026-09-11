package com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.aporte;

import com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.Dinheiro;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class Aporte {

    private final UUID id;
    private final UUID cofrinhoId;
    private final Dinheiro valor;
    private final LocalDateTime dataRegistro;

    private Aporte(UUID id, UUID cofrinhoId, Dinheiro valor, LocalDateTime dataRegistro) {
        if (cofrinhoId == null) {
            throw new IllegalArgumentException("Aporte precisa estar associado a um cofrinho");
        }
        if (valor == null || valor.valor().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor do aporte deve ser maior que zero");
        }
        this.id = id;
        this.cofrinhoId = cofrinhoId;
        this.valor = valor;
        this.dataRegistro = dataRegistro;
    }
    public static Aporte registrar(UUID cofrinhoId, Dinheiro valor) {
        return new Aporte(UUID.randomUUID(), cofrinhoId, valor, LocalDateTime.now());
    }
    public UUID getId() { return id; }
    public UUID getCofrinhoId() { return cofrinhoId; }
    public Dinheiro getValor() { return valor; }
    public LocalDateTime getDataRegistro() { return dataRegistro; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Aporte outro)) return false;
        return this.id.equals(outro.id);
    }
    @Override
    public int hashCode() {
        return Objects.hash(id);

    }

}

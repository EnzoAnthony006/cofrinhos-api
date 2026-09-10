package com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain;

import java.math.BigDecimal;
import java.util.Objects;

public class Dinheiro {

    private final BigDecimal valor;

    private Dinheiro(BigDecimal valor) {
        if (valor == null) {
            throw new IllegalArgumentException("Valor não pode ser nulo");
        }
        if (valor.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Valor não pode ser negativo");
        }
        this.valor = valor;
    }
    public static Dinheiro de(BigDecimal valor) {
        return new Dinheiro(valor);
    }

    public static Dinheiro zero() {
        return new Dinheiro(BigDecimal.ZERO);
    }

    public Dinheiro somar(Dinheiro outro) {
        return  new Dinheiro(this.valor.add(outro.valor));


    }

    public boolean maiorOuIgualA(Dinheiro outro) {
        return  this.valor.compareTo(outro.valor) >= 0;

    }
    public BigDecimal valor() {
        return valor;
    }

    @Override
    public boolean equals (Object o) {
        if (this == o) return true;
        if (!(o instanceof Dinheiro outro)) return false;
        return this.valor.compareTo(outro.valor) == 0;

    }
    @Override
    public int hashCode () {
        return Objects.hash(valor.stripTrailingZeros());
    }
    @Override
    public String toString () {
        return "R$ " + valor.toString();
    }

}

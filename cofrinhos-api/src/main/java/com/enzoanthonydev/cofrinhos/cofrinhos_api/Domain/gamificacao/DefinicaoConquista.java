package com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.gamificacao;

public enum DefinicaoConquista {

    PRIMEIRO_COFRINHO_CONCLUIDO(
            "Primeiro cofrinho concluído",
            "Você completou a meta de um cofrinho pela primeira vez"),

    PRIMEIRA_RESERVA_EMERGENCIA_COMPLETA(
            "Reserva de emergência completa",
            "Você completou um cofrinho da categoria Reserva de Emergência"),

    STREAK_QUATRO_SEMANAS(
            "Streak de 4 semanas",
            "Você manteve uma sequência de 4 semanas seguidas de aportes");

    private final String titulo;
    private final String descricao;

    DefinicaoConquista(String titulo, String descricao) {
        this.titulo = titulo;
        this.descricao = descricao;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescricao() {
        return descricao;
    }
}

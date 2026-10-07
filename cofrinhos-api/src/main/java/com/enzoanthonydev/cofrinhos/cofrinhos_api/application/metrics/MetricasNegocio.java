package com.enzoanthonydev.cofrinhos.cofrinhos_api.application.metrics;

public interface MetricasNegocio {

    void aporteRegistrado(String categoria);

    void xpConcedido(int xp);

    void conquistaDesbloqueada(String conquista);

    void falhaAoAtualizarRanking();
}
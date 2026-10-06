package com.enzoanthonydev.cofrinhos.cofrinhos_api.adapters.inbound.rest;

import com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.gamificacao.ConquistaDesbloqueada;
import com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.gamificacao.DefinicaoConquista;

import java.time.LocalDateTime;

public record ConquistaResponse(
        DefinicaoConquista definicao,
        LocalDateTime dataDesbloqueio
) {
    public static ConquistaResponse de(ConquistaDesbloqueada conquistaDesbloqueada) {
        return new ConquistaResponse(conquistaDesbloqueada.getDefinicao(), conquistaDesbloqueada.getDataDesbloqueio());
    }
}

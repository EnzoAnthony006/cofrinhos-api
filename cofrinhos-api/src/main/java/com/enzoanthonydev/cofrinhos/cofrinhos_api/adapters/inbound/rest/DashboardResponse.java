package com.enzoanthonydev.cofrinhos.cofrinhos_api.adapters.inbound.rest;

import com.enzoanthonydev.cofrinhos.cofrinhos_api.application.gamificacao.PosicaoRanking;

import java.util.List;
import java.util.UUID;

public record DashboardResponse(
        UUID usuarioId,
        PerfilXPResponse perfil,
        List<ConquistaResponse> conquistas,
        PosicaoRanking posicaoNoRanking
) {
}

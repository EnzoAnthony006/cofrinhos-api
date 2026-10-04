package com.enzoanthonydev.cofrinhos.cofrinhos_api.adapters.inbound.rest;

import com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.CategoriaInvestimento;
import com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.Cofrinho;
import com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.StatusCofrinho;

import java.math.BigDecimal;
import java.util.UUID;
import com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.CategoriaInvestimento;
import com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.Cofrinho;
import com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.StatusCofrinho;
import com.enzoanthonydev.cofrinhos.cofrinhos_api.application.CofrinhoResumo;

import java.math.BigDecimal;
import java.util.UUID;

public record CofrinhoResponse(
        UUID id,
        UUID usuarioId,
        String nome,
        String descricao,
        CategoriaInvestimento categoria,
        BigDecimal valorMeta,
        BigDecimal valorAcumulado,
        StatusCofrinho status,
        double progresso
) {

    public static CofrinhoResponse de(Cofrinho cofrinho) {
        return new CofrinhoResponse(
                cofrinho.getId(),
                cofrinho.getUsuarioId(),
                cofrinho.getNome(),
                cofrinho.getDescricao(),
                cofrinho.getCategoria(),
                cofrinho.getValorMeta().valor(),
                cofrinho.getValorAcumulado().valor(),
                cofrinho.getStatus(),
                cofrinho.progresso()
        );
    }

    public static CofrinhoResponse de(CofrinhoResumo resumo) {
        return new CofrinhoResponse(
                resumo.id(),
                resumo.usuarioId(),
                resumo.nome(),
                resumo.descricao(),
                resumo.categoria(),
                resumo.valorMeta(),
                resumo.valorAcumulado(),
                resumo.status(),
                resumo.progresso()
        );
    }
}

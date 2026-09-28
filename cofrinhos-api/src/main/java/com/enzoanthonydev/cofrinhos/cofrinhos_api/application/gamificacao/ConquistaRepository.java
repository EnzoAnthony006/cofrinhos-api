package com.enzoanthonydev.cofrinhos.cofrinhos_api.application.gamificacao;

import com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.gamificacao.ConquistaDesbloqueada;
import com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.gamificacao.DefinicaoConquista;

import java.util.List;
import java.util.UUID;

public interface ConquistaRepository {
    ConquistaDesbloqueada salvar(ConquistaDesbloqueada conquista);

    List<ConquistaDesbloqueada> buscarPorUsuarioId(UUID usuarioId);

    boolean existeConquista(UUID usuarioId, DefinicaoConquista definicaoConquista);
}

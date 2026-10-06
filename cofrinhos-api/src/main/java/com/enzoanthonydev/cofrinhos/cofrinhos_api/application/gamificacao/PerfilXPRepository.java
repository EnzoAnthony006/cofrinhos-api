package com.enzoanthonydev.cofrinhos.cofrinhos_api.application.gamificacao;

import com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.gamificacao.PerfilXP;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PerfilXPRepository {
    PerfilXP salvar(PerfilXP perfilXP);
    Optional<PerfilXP> buscarPorUsuarioId(UUID usuarioId);
    List<PerfilXP> buscarTodos();
}
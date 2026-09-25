package com.enzoanthonydev.cofrinhos.cofrinhos_api.application.gamificacao;

import com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.gamificacao.PerfilXP;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class BuscarPerfilXPUseCase {

    private final PerfilXPRepository perfilXPRepository;

    public BuscarPerfilXPUseCase(PerfilXPRepository perfilXPRepository) {
        this.perfilXPRepository = perfilXPRepository;
    }

    public PerfilXP executar(UUID usuarioId) {
        return perfilXPRepository.buscarPorUsuarioId(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Perfil de XP não encontrado para o usuário: " + usuarioId));
    }
}

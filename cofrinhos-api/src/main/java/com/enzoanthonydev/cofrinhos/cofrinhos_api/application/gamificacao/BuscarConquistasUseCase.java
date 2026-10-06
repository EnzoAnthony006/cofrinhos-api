package com.enzoanthonydev.cofrinhos.cofrinhos_api.application.gamificacao;

import com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.gamificacao.ConquistaDesbloqueada;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class BuscarConquistasUseCase {

    private final ConquistaRepository conquistaRepository;

    public BuscarConquistasUseCase(ConquistaRepository conquistaRepository) {
        this.conquistaRepository = conquistaRepository;

    }
    public List<ConquistaDesbloqueada> executar (UUID usuarioId){
      return conquistaRepository.buscarPorUsuarioId(usuarioId);
    }
}

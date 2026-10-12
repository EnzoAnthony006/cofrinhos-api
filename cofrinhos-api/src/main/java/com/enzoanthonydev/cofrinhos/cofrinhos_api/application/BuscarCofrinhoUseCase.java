package com.enzoanthonydev.cofrinhos.cofrinhos_api.application;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class BuscarCofrinhoUseCase {

    private final CofrinhoRepository cofrinhoRepository;

    public BuscarCofrinhoUseCase(CofrinhoRepository cofrinhoRepository) {
        this.cofrinhoRepository = cofrinhoRepository;
    }

    @Cacheable(cacheNames = CacheNames.COFRINHOS, key = "#id.toString() + ':' + #usuarioId.toString()")
    public CofrinhoResumo executar(UUID id, UUID usuarioId) {
        return cofrinhoRepository.buscarPorId(id)
                .filter(cofrinho -> cofrinho.getUsuarioId().equals(usuarioId))
                .map(CofrinhoResumo::de)
                .orElseThrow(() -> new CofrinhoNaoEncontradoException(id));
    }
}

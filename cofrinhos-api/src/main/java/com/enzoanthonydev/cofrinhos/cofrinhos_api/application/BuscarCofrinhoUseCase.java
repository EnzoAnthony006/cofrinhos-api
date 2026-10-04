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

    @Cacheable(cacheNames = CacheNames.COFRINHOS, key = "#id.toString()")
    public CofrinhoResumo executar(UUID id) {
        return cofrinhoRepository.buscarPorId(id)
                .map(CofrinhoResumo::de)
                .orElseThrow(() -> new IllegalArgumentException("Cofrinho não encontrado: " + id));
    }
}

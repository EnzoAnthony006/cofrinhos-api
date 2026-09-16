package com.enzoanthonydev.cofrinhos.cofrinhos_api.application;

import com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.Cofrinho;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class BuscarCofrinhoUseCase {

    private final CofrinhoRepository cofrinhoRepository;

    public BuscarCofrinhoUseCase(CofrinhoRepository cofrinhoRepository) {
        this.cofrinhoRepository = cofrinhoRepository;
    }
    public Cofrinho executar(UUID id) {
        return cofrinhoRepository.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Cofrinho não encontrado: " + id ));
    }
}

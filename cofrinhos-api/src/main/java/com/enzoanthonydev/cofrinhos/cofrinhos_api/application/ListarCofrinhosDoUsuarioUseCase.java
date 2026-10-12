package com.enzoanthonydev.cofrinhos.cofrinhos_api.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ListarCofrinhosDoUsuarioUseCase {

    private final CofrinhoRepository cofrinhoRepository;

    public ListarCofrinhosDoUsuarioUseCase(CofrinhoRepository cofrinhoRepository) {
        this.cofrinhoRepository = cofrinhoRepository;
    }

    @Transactional(readOnly = true)
    public List<CofrinhoResumo> executar(UUID usuarioId) {
        return cofrinhoRepository.buscarPorUsuarioId(usuarioId)
                .stream()
                .map(CofrinhoResumo::de)
                .toList();
    }
}

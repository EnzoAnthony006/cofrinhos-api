package com.enzoanthonydev.cofrinhos.cofrinhos_api.application;


import com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.CategoriaInvestimento;
import com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.Cofrinho;
import com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.Dinheiro;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CriarCofrinhoUseCase {

    private final CofrinhoRepository cofrinhoRepository;

    public CriarCofrinhoUseCase(CofrinhoRepository cofrinhoRepository) {
        this.cofrinhoRepository = cofrinhoRepository;
    }
    public Cofrinho executar (UUID usuarioId, String nome, String descricao,
                              CategoriaInvestimento categoria, Dinheiro valorMeta) {
        Cofrinho cofrinho = Cofrinho.criar(usuarioId, nome, descricao, categoria, valorMeta);
        return cofrinhoRepository.salvar(cofrinho);
    }
}

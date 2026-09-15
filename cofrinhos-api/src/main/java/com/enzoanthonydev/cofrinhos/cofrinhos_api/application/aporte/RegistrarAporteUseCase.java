package com.enzoanthonydev.cofrinhos.cofrinhos_api.application.aporte;

import com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.Cofrinho;
import com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.Dinheiro;
import com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.aporte.Aporte;
import com.enzoanthonydev.cofrinhos.cofrinhos_api.application.CofrinhoRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class RegistrarAporteUseCase {

    private final CofrinhoRepository cofrinhoRepository;
    private final AporteRepository aporteRepository;

    public RegistrarAporteUseCase(CofrinhoRepository cofrinhoRepository, AporteRepository aporteRepository) {
        this.cofrinhoRepository = cofrinhoRepository;
        this.aporteRepository = aporteRepository;
    }

    @Transactional
    public Aporte executar(UUID cofrinhoId, Dinheiro valor) {
        Cofrinho cofrinho = cofrinhoRepository.buscarPorId(cofrinhoId)
                .orElseThrow(() -> new IllegalArgumentException("Cofrinho não encontrado: " + cofrinhoId));

        cofrinho.registrarAporte(valor);
        cofrinhoRepository.salvar(cofrinho);

        Aporte aporte = Aporte.registrar(cofrinhoId, valor);
        return aporteRepository.salvar(aporte);
    }
}

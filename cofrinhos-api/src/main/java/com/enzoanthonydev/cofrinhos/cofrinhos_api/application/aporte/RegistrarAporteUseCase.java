package com.enzoanthonydev.cofrinhos.cofrinhos_api.application.aporte;

import com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.Cofrinho;
import com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.Dinheiro;
import com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.aporte.Aporte;
import com.enzoanthonydev.cofrinhos.cofrinhos_api.application.CofrinhoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class RegistrarAporteUseCase {

    private final CofrinhoRepository cofrinhoRepository;
    private final AporteRepository aporteRepository;
    private final AporteEventPublisher aporteEventPublisher;

    public RegistrarAporteUseCase(CofrinhoRepository cofrinhoRepository,
                                  AporteRepository aporteRepository,
                                  AporteEventPublisher aporteEventPublisher) {
        this.cofrinhoRepository = cofrinhoRepository;
        this.aporteRepository = aporteRepository;
        this.aporteEventPublisher = aporteEventPublisher;
    }

    @Transactional
    public Aporte executar(UUID cofrinhoId, Dinheiro valor) {
        Cofrinho cofrinho = cofrinhoRepository.buscarPorId(cofrinhoId)
                .orElseThrow(() -> new IllegalArgumentException("Cofrinho não encontrado: " + cofrinhoId));

        cofrinho.registrarAporte(valor);
        cofrinhoRepository.salvar(cofrinho);

        Aporte aporte = Aporte.registrar(cofrinhoId, valor);
        Aporte aporteSalvo = aporteRepository.salvar(aporte);

        aporteEventPublisher.publicar(new AporteRegistradoEvent(
                aporteSalvo.getId(),
                cofrinhoId,
                cofrinho.getUsuarioId(),
                valor.valor(),
                aporteSalvo.getDataRegistro()
        ));

        return aporteSalvo;
    }
}

package com.enzoanthonydev.cofrinhos.cofrinhos_api.adapters.inbound.outbound.persistence;

import com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.Cofrinho;
import com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.Dinheiro;
import com.enzoanthonydev.cofrinhos.cofrinhos_api.application.CofrinhoRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class CofrinhoRepositoryJpaAdapter implements CofrinhoRepository {

    private final CofrinhoSpringDataRepository springDataRepository;

    public CofrinhoRepositoryJpaAdapter(CofrinhoSpringDataRepository springDataRepository) {
        this.springDataRepository = springDataRepository;
    }

    @Override
    public Cofrinho salvar(Cofrinho cofrinho) {
        CofrinhoJpaEntity entity = paraEntity(cofrinho);
        CofrinhoJpaEntity salvo = springDataRepository.save(entity);
        return paraDominio(salvo);
    }

    @Override
    public Optional<Cofrinho> buscarPorId(UUID id) {
        return springDataRepository.findById(id).map(this::paraDominio);
    }

    private CofrinhoJpaEntity paraEntity(Cofrinho cofrinho) {
        return new CofrinhoJpaEntity(
                cofrinho.getId(),
                cofrinho.getUsuarioId(),
                cofrinho.getNome(),
                cofrinho.getDescricao(),
                cofrinho.getCategoria(),
                cofrinho.getValorMeta().valor(),
                cofrinho.getValorAcumulado().valor(),
                cofrinho.getStatus()
        );
    }

    private Cofrinho paraDominio(CofrinhoJpaEntity entity) {
        return Cofrinho.reconstruir(
                entity.getId(),
                entity.getUsuarioId(),
                entity.getNome(),
                entity.getDescricao(),
                entity.getCategoria(),
                Dinheiro.de(entity.getValorMeta()),
                Dinheiro.de(entity.getValorAcumulado()),
                entity.getStatus()
        );
    }
}



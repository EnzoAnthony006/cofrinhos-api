package com.enzoanthonydev.cofrinhos.cofrinhos_api.adapters.inbound.outbound.persistence;

import com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.Dinheiro;
import com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.aporte.Aporte;
import com.enzoanthonydev.cofrinhos.cofrinhos_api.application.aporte.AporteRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class AporteRepositoryJpaAdapter implements AporteRepository {

    private final AporteSpringDataRepository springDataRepository;

    public AporteRepositoryJpaAdapter(AporteSpringDataRepository springDataRepository) {
        this.springDataRepository = springDataRepository;
    }
    @Override
    public Aporte salvar (Aporte aporte) {
        AporteJpaEntity entitySalva = springDataRepository.save(paraEntity(aporte));
        return paraDominio(entitySalva);
    }
    @Override
    public List<Aporte> buscarPorCofrinhoId(UUID cofrinhoId) {
        return springDataRepository.findByCofrinhoId(cofrinhoId)
                .stream()
                .map(this::paraDominio)
                .toList();
    }
    private Aporte paraDominio(AporteJpaEntity entity) {
        return Aporte.reconstruir(
                entity.getId(),
                entity.getCofrinhoId(),
                Dinheiro.de(entity.getValor()),
                entity.getDataRegistro()
        );
    }
    private AporteJpaEntity paraEntity(Aporte aporte) {
        return new AporteJpaEntity(
                aporte.getId(),
                aporte.getCofrinhoId(),
                aporte.getValor().valor(),
                aporte.getDataRegistro()
        );
    }
}

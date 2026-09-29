package com.enzoanthonydev.cofrinhos.cofrinhos_api.adapters.inbound.outbound.persistence;

import com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.gamificacao.ConquistaDesbloqueada;
import com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.gamificacao.DefinicaoConquista;
import com.enzoanthonydev.cofrinhos.cofrinhos_api.application.gamificacao.ConquistaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class ConquistaRepositoryJpaAdapter implements ConquistaRepository {

    private final ConquistaSpringDataRepository springDataRepository;

    public ConquistaRepositoryJpaAdapter(ConquistaSpringDataRepository springDataRepository) {
        this.springDataRepository = springDataRepository;
    }

    @Override
    public ConquistaDesbloqueada salvar(ConquistaDesbloqueada conquista) {
        ConquistaJpaEntity entitySalva = springDataRepository.save(paraEntity(conquista));
        return paraDominio(entitySalva);
    }

    @Override
    public List<ConquistaDesbloqueada> buscarPorUsuarioId(UUID usuarioId) {
        return springDataRepository.findByUsuarioId(usuarioId).stream()
                .map(this::paraDominio)
                .toList();
    }

    @Override
    public boolean existeConquista(UUID usuarioId, DefinicaoConquista definicao) {
        return springDataRepository.existsByUsuarioIdAndDefinicao(usuarioId, definicao);
    }

    private ConquistaJpaEntity paraEntity(ConquistaDesbloqueada conquista) {
        return new ConquistaJpaEntity(
                conquista.getId(),
                conquista.getUsuarioId(),
                conquista.getDefinicao(),
                conquista.getDataDesbloqueio()
        );
    }

    private ConquistaDesbloqueada paraDominio(ConquistaJpaEntity entity) {
        return ConquistaDesbloqueada.reconstruir(
                entity.getId(),
                entity.getUsuarioId(),
                entity.getDefinicao(),
                entity.getDataDesbloqueio()
        );
    }
}

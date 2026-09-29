package com.enzoanthonydev.cofrinhos.cofrinhos_api.adapters.inbound.outbound.persistence;

import com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.gamificacao.DefinicaoConquista;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ConquistaSpringDataRepository extends JpaRepository<ConquistaJpaEntity, UUID> {

    List<ConquistaJpaEntity> findByUsuarioId(UUID usuarioId);

    boolean existsByUsuarioIdAndDefinicao(UUID usuarioId, DefinicaoConquista definicao);
}

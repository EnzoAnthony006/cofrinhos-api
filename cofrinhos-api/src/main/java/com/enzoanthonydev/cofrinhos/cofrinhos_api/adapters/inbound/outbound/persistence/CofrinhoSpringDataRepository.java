package com.enzoanthonydev.cofrinhos.cofrinhos_api.adapters.inbound.outbound.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CofrinhoSpringDataRepository extends JpaRepository<CofrinhoJpaEntity, UUID> {

    List<CofrinhoJpaEntity> findByUsuarioIdOrderByNomeAsc(UUID usuarioId);
}

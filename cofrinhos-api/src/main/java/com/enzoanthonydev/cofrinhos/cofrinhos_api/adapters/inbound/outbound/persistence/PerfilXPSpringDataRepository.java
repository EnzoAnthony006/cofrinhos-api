package com.enzoanthonydev.cofrinhos.cofrinhos_api.adapters.inbound.outbound.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PerfilXPSpringDataRepository extends JpaRepository<PerfilXPJpaEntity, UUID> {
}

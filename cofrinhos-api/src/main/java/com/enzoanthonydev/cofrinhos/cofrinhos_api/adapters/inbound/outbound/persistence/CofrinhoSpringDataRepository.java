package com.enzoanthonydev.cofrinhos.cofrinhos_api.adapters.inbound.outbound.persistence;

import com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.Cofrinho;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CofrinhoSpringDataRepository  extends JpaRepository<CofrinhoJpaEntity, UUID> {
}

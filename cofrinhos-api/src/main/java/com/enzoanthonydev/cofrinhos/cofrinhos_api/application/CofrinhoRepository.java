package com.enzoanthonydev.cofrinhos.cofrinhos_api.application;

import com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.Cofrinho;

import java.util.Optional;
import java.util.UUID;

public interface  CofrinhoRepository {

    Cofrinho salvar (Cofrinho cofrinho);

    Optional<Cofrinho> buscarPorId (UUID id);
}

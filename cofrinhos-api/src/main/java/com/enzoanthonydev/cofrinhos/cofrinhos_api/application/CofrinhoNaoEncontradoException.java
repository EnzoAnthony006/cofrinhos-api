package com.enzoanthonydev.cofrinhos.cofrinhos_api.application;

import java.util.UUID;

public class CofrinhoNaoEncontradoException  extends RuntimeException {

    public CofrinhoNaoEncontradoException(UUID id) {
        super ("Cofrinho não encontrado: " + id);
    }
}

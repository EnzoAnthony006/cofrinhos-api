package com.enzoanthonydev.cofrinhos.cofrinhos_api.adapters.inbound.rest;

import com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.Cofrinho;
import com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.Dinheiro;
import com.enzoanthonydev.cofrinhos.cofrinhos_api.application.CriarCofrinhoUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cofrinhos")
public class CofrinhoController {

    private final CriarCofrinhoUseCase criarCofrinhoUseCase;

    public CofrinhoController(CriarCofrinhoUseCase criarCofrinhoUseCase) {
        this.criarCofrinhoUseCase = criarCofrinhoUseCase;
    }

    @PostMapping
    public ResponseEntity<CofrinhoResponse> criar(@Valid @RequestBody CriarCofrinhoRequest request) {
        Cofrinho cofrinho = criarCofrinhoUseCase.executar(
                request.usuarioId(),
                request.nome(),
                request.descricao(),
                request.categoria(),
                Dinheiro.de(request.valorMeta())
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(CofrinhoResponse.de(cofrinho));
    }
}

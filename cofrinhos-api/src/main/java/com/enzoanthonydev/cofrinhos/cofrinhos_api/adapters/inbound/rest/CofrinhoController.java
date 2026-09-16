package com.enzoanthonydev.cofrinhos.cofrinhos_api.adapters.inbound.rest;

import com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.Cofrinho;
import com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.Dinheiro;
import com.enzoanthonydev.cofrinhos.cofrinhos_api.application.BuscarCofrinhoUseCase;
import com.enzoanthonydev.cofrinhos.cofrinhos_api.application.CriarCofrinhoUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/cofrinhos")
public class CofrinhoController {

    private final CriarCofrinhoUseCase criarCofrinhoUseCase;
    private final BuscarCofrinhoUseCase buscarCofrinhoUseCase;

    public CofrinhoController(CriarCofrinhoUseCase criarCofrinhoUseCase, BuscarCofrinhoUseCase buscarCofrinhoUseCase) {
        this.criarCofrinhoUseCase = criarCofrinhoUseCase;
        this.buscarCofrinhoUseCase = buscarCofrinhoUseCase;
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

    @GetMapping("/{id}")
    public ResponseEntity<CofrinhoResponse> buscarPorId(@PathVariable UUID id) {
        Cofrinho cofrinho = buscarCofrinhoUseCase.executar(id);
        return ResponseEntity.ok(CofrinhoResponse.de(cofrinho));
    }
}

package com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.cofrinho;

import com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.CategoriaInvestimento;
import com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.Cofrinho;
import com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.Dinheiro;
import com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.StatusCofrinho;
import com.enzoanthonydev.cofrinhos.cofrinhos_api.adapters.inbound.outbound.persistence.CofrinhoRepositoryJpaAdapter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(CofrinhoRepositoryJpaAdapter.class)
@Testcontainers
class CofrinhoRepositoryJpaAdapterTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("cofrinhos")
            .withUsername("cofrinhos")
            .withPassword("cofrinhos");

    @DynamicPropertySource
    static void propriedadesDoBanco(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private CofrinhoRepositoryJpaAdapter cofrinhoRepository;

    @Test
    void deveSalvarEBuscarCofrinhoPorId() {
        Cofrinho cofrinho = Cofrinho.criar(UUID.randomUUID(), "Viagem", "descricao",
                CategoriaInvestimento.ECONOMIA, Dinheiro.de(new BigDecimal("1000")));

        cofrinhoRepository.salvar(cofrinho);

        Optional<Cofrinho> encontrado = cofrinhoRepository.buscarPorId(cofrinho.getId());

        assertTrue(encontrado.isPresent());
        assertEquals(cofrinho.getId(), encontrado.get().getId());
        assertEquals(cofrinho.getNome(), encontrado.get().getNome());
        assertEquals(cofrinho.getValorMeta(), encontrado.get().getValorMeta());
        assertEquals(StatusCofrinho.ATIVO, encontrado.get().getStatus());
    }

    @Test
    void deveRetornarVazioQuandoCofrinhoNaoExiste() {
        Optional<Cofrinho> encontrado = cofrinhoRepository.buscarPorId(UUID.randomUUID());

        assertTrue(encontrado.isEmpty());
    }

    @Test
    void deveAtualizarCofrinhoExistenteAoSalvarNovamente() {
        Cofrinho cofrinho = Cofrinho.criar(UUID.randomUUID(), "Viagem", "descricao",
                CategoriaInvestimento.RESERVA_EMERGENCIA, Dinheiro.de(new BigDecimal("100")));
        cofrinhoRepository.salvar(cofrinho);

        cofrinho.registrarAporte(Dinheiro.de(new BigDecimal("150")));
        cofrinhoRepository.salvar(cofrinho);

        Optional<Cofrinho> encontrado = cofrinhoRepository.buscarPorId(cofrinho.getId());

        assertTrue(encontrado.isPresent());
        assertEquals(StatusCofrinho.CONCLUIDO, encontrado.get().getStatus());
        assertEquals(Dinheiro.de(new BigDecimal("150")), encontrado.get().getValorAcumulado());
    }
}

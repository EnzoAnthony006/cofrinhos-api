package com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.cofrinho;

import com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.gamificacao.ConquistaDesbloqueada;
import com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.gamificacao.DefinicaoConquista;
import com.enzoanthonydev.cofrinhos.cofrinhos_api.adapters.inbound.outbound.persistence.ConquistaRepositoryJpaAdapter;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(ConquistaRepositoryJpaAdapter.class)
@Testcontainers
class ConquistaRepositoryJpaAdapterTest {

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
    private ConquistaRepositoryJpaAdapter conquistaRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void deveSalvarEReconstruirConquistaComTodosOsCampos() {
        UUID id = UUID.randomUUID();
        UUID usuarioId = UUID.randomUUID();
        LocalDateTime data = LocalDateTime.of(2026, 9, 28, 10, 30, 0);
        ConquistaDesbloqueada conquista = ConquistaDesbloqueada.reconstruir(
                id, usuarioId, DefinicaoConquista.PRIMEIRO_COFRINHO_CONCLUIDO, data);

        conquistaRepository.salvar(conquista);
        forcarIdaAoBanco();

        List<ConquistaDesbloqueada> resultado = conquistaRepository.buscarPorUsuarioId(usuarioId);

        assertEquals(1, resultado.size());
        ConquistaDesbloqueada encontrada = resultado.get(0);
        assertEquals(id, encontrada.getId());
        assertEquals(usuarioId, encontrada.getUsuarioId());
        assertEquals(DefinicaoConquista.PRIMEIRO_COFRINHO_CONCLUIDO, encontrada.getDefinicao());
        assertEquals(data, encontrada.getDataDesbloqueio());
    }

    @Test
    void deveBuscarSomenteConquistasDoUsuarioInformado() {
        UUID usuarioA = UUID.randomUUID();
        UUID usuarioB = UUID.randomUUID();
        conquistaRepository.salvar(ConquistaDesbloqueada.desbloquear(usuarioA, DefinicaoConquista.PRIMEIRO_COFRINHO_CONCLUIDO));
        conquistaRepository.salvar(ConquistaDesbloqueada.desbloquear(usuarioA, DefinicaoConquista.STREAK_QUATRO_SEMANAS));
        conquistaRepository.salvar(ConquistaDesbloqueada.desbloquear(usuarioB, DefinicaoConquista.PRIMEIRO_COFRINHO_CONCLUIDO));
        forcarIdaAoBanco();

        List<ConquistaDesbloqueada> doUsuarioA = conquistaRepository.buscarPorUsuarioId(usuarioA);
        List<ConquistaDesbloqueada> doUsuarioB = conquistaRepository.buscarPorUsuarioId(usuarioB);

        Set<DefinicaoConquista> definicoesA = doUsuarioA.stream()
                .map(ConquistaDesbloqueada::getDefinicao)
                .collect(Collectors.toSet());
        assertEquals(2, doUsuarioA.size());
        assertEquals(Set.of(DefinicaoConquista.PRIMEIRO_COFRINHO_CONCLUIDO, DefinicaoConquista.STREAK_QUATRO_SEMANAS), definicoesA);
        assertEquals(1, doUsuarioB.size());
    }

    @Test
    void deveRetornarListaVaziaParaUsuarioSemConquistas() {
        List<ConquistaDesbloqueada> resultado = conquistaRepository.buscarPorUsuarioId(UUID.randomUUID());

        assertTrue(resultado.isEmpty());
    }

    @Test
    void deveInformarSeConquistaJaExisteParaUsuarioEDefinicao() {
        UUID usuarioId = UUID.randomUUID();
        conquistaRepository.salvar(ConquistaDesbloqueada.desbloquear(usuarioId, DefinicaoConquista.PRIMEIRO_COFRINHO_CONCLUIDO));
        forcarIdaAoBanco();

        assertTrue(conquistaRepository.existeConquista(usuarioId, DefinicaoConquista.PRIMEIRO_COFRINHO_CONCLUIDO));
        assertFalse(conquistaRepository.existeConquista(usuarioId, DefinicaoConquista.STREAK_QUATRO_SEMANAS));
        assertFalse(conquistaRepository.existeConquista(UUID.randomUUID(), DefinicaoConquista.PRIMEIRO_COFRINHO_CONCLUIDO));
    }

    @Test
    void deveImpedirDuasConquistasIguaisParaOMesmoUsuario() {
        UUID usuarioId = UUID.randomUUID();
        conquistaRepository.salvar(ConquistaDesbloqueada.desbloquear(usuarioId, DefinicaoConquista.STREAK_QUATRO_SEMANAS));
        conquistaRepository.salvar(ConquistaDesbloqueada.desbloquear(usuarioId, DefinicaoConquista.STREAK_QUATRO_SEMANAS));

        assertThrows(PersistenceException.class, entityManager::flush);
    }

    private void forcarIdaAoBanco() {
        entityManager.flush();
        entityManager.clear();
    }
}

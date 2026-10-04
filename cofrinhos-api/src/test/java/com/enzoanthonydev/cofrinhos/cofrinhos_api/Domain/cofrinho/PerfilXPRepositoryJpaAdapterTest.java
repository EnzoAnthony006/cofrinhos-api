package com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.cofrinho;

import com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.gamificacao.PerfilXP;
import com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.gamificacao.Streak;
import com.enzoanthonydev.cofrinhos.cofrinhos_api.adapters.inbound.outbound.persistence.PerfilXPRepositoryJpaAdapter;
import jakarta.persistence.EntityManager;
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

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(PerfilXPRepositoryJpaAdapter.class)
@Testcontainers
class PerfilXPRepositoryJpaAdapterTest {

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
    private PerfilXPRepositoryJpaAdapter perfilXPRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void deveSalvarEBuscarPerfilPorUsuarioId() {
        UUID usuarioId = UUID.randomUUID();
        Streak streak = Streak.reconstruir(3, 5, LocalDate.of(2026, 9, 28));
        PerfilXP perfil = PerfilXP.reconstruir(usuarioId, 150, streak);

        perfilXPRepository.salvar(perfil);
        forcarIdaAoBanco();

        Optional<PerfilXP> encontrado = perfilXPRepository.buscarPorUsuarioId(usuarioId);

        assertTrue(encontrado.isPresent());
        PerfilXP resultado = encontrado.get();
        assertEquals(usuarioId, resultado.getUsuarioId());
        assertEquals(150, resultado.getXpTotal());
        assertEquals(3, resultado.getStreakGlobal().getSequenciaAtual());
        assertEquals(5, resultado.getStreakGlobal().getMelhorSequencia());
        assertEquals(LocalDate.of(2026, 9, 28), resultado.getStreakGlobal().getDataUltimaAtividade());
    }

    @Test
    void deveRetornarVazioQuandoPerfilNaoExiste() {
        Optional<PerfilXP> encontrado = perfilXPRepository.buscarPorUsuarioId(UUID.randomUUID());

        assertTrue(encontrado.isEmpty());
    }

    @Test
    void deveAtualizarPerfilExistenteAoSalvarNovamente() {
        UUID usuarioId = UUID.randomUUID();
        PerfilXP inicial = PerfilXP.reconstruir(
                usuarioId, 100, Streak.reconstruir(1, 1, LocalDate.of(2026, 9, 14)));
        perfilXPRepository.salvar(inicial);
        forcarIdaAoBanco();

        PerfilXP atualizado = PerfilXP.reconstruir(
                usuarioId, 250, Streak.reconstruir(4, 4, LocalDate.of(2026, 10, 5)));
        perfilXPRepository.salvar(atualizado);
        forcarIdaAoBanco();

        PerfilXP resultado = perfilXPRepository.buscarPorUsuarioId(usuarioId).orElseThrow();
        assertEquals(250, resultado.getXpTotal());
        assertEquals(4, resultado.getStreakGlobal().getSequenciaAtual());
        assertEquals(4, resultado.getStreakGlobal().getMelhorSequencia());
        assertEquals(LocalDate.of(2026, 10, 5), resultado.getStreakGlobal().getDataUltimaAtividade());
    }

    private void forcarIdaAoBanco() {
        entityManager.flush();
        entityManager.clear();
    }
}
package com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.cofrinho;

import com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.CategoriaInvestimento;
import com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.Cofrinho;
import com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.Dinheiro;
import com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.StatusCofrinho;
import com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.aporte.Aporte;
import com.enzoanthonydev.cofrinhos.cofrinhos_api.application.CofrinhoRepository;
import com.enzoanthonydev.cofrinhos.cofrinhos_api.application.aporte.AporteEventPublisher;
import com.enzoanthonydev.cofrinhos.cofrinhos_api.application.aporte.AporteRegistradoEvent;
import com.enzoanthonydev.cofrinhos.cofrinhos_api.application.aporte.AporteRepository;
import com.enzoanthonydev.cofrinhos.cofrinhos_api.application.aporte.RegistrarAporteUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegistrarAporteUseCaseTest {

    @Mock
    private CofrinhoRepository cofrinhoRepository;

    @Mock
    private AporteRepository aporteRepository;

    @Mock
    private AporteEventPublisher aporteEventPublisher;

    private RegistrarAporteUseCase useCase() {
        return new RegistrarAporteUseCase(cofrinhoRepository, aporteRepository, aporteEventPublisher);
    }

    @Test
    void deveRegistrarAporteEPublicarEventoComDadosDoCofrinho() {
        UUID cofrinhoId = UUID.randomUUID();
        Cofrinho cofrinho = Cofrinho.criar(UUID.randomUUID(), "Viagem", "descricao",
                CategoriaInvestimento.ECONOMIA, Dinheiro.de(new BigDecimal("1000")));

        when(cofrinhoRepository.buscarPorId(cofrinhoId)).thenReturn(Optional.of(cofrinho));
        when(aporteRepository.salvar(any(Aporte.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Dinheiro valor = Dinheiro.de(new BigDecimal("250"));
        Aporte resultado = useCase().executar(cofrinhoId, valor);

        assertEquals(cofrinhoId, resultado.getCofrinhoId());
        assertEquals(valor, resultado.getValor());

        verify(cofrinhoRepository).salvar(cofrinho);

        ArgumentCaptor<AporteRegistradoEvent> captor = ArgumentCaptor.forClass(AporteRegistradoEvent.class);
        verify(aporteEventPublisher).publicar(captor.capture());

        AporteRegistradoEvent evento = captor.getValue();
        assertEquals(cofrinhoId, evento.cofrinhoId());
        assertEquals(cofrinho.getUsuarioId(), evento.usuarioId());
        assertEquals(valor.valor(), evento.valor());
        assertEquals(StatusCofrinho.ATIVO, evento.statusCofrinho());
        assertEquals(CategoriaInvestimento.ECONOMIA, evento.categoriaCofrinho());
    }

    @Test
    void deveMarcarEventoComStatusConcluidoQuandoAporteBateAMeta() {
        UUID cofrinhoId = UUID.randomUUID();
        Cofrinho cofrinho = Cofrinho.criar(UUID.randomUUID(), "Viagem", "descricao",
                CategoriaInvestimento.RESERVA_EMERGENCIA, Dinheiro.de(new BigDecimal("100")));

        when(cofrinhoRepository.buscarPorId(cofrinhoId)).thenReturn(Optional.of(cofrinho));
        when(aporteRepository.salvar(any(Aporte.class))).thenAnswer(invocation -> invocation.getArgument(0));

        useCase().executar(cofrinhoId, Dinheiro.de(new BigDecimal("150")));

        ArgumentCaptor<AporteRegistradoEvent> captor = ArgumentCaptor.forClass(AporteRegistradoEvent.class);
        verify(aporteEventPublisher).publicar(captor.capture());

        assertEquals(StatusCofrinho.CONCLUIDO, captor.getValue().statusCofrinho());
        assertEquals(CategoriaInvestimento.RESERVA_EMERGENCIA, captor.getValue().categoriaCofrinho());
    }

    @Test
    void deveLancarExcecaoQuandoCofrinhoNaoEncontrado() {
        UUID cofrinhoId = UUID.randomUUID();
        when(cofrinhoRepository.buscarPorId(cofrinhoId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                useCase().executar(cofrinhoId, Dinheiro.de(new BigDecimal("100"))));
    }
}

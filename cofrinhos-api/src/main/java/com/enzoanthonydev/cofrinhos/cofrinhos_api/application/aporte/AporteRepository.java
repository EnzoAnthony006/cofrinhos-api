package com.enzoanthonydev.cofrinhos.cofrinhos_api.application.aporte;

import com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.aporte.Aporte;

import java.util.List;
import java.util.UUID;

public interface AporteRepository {

    Aporte salvar(Aporte aporte);

    List<Aporte> buscarPorCofrinhoId(UUID cofrinhoId);
}

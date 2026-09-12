package com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.gamificacao;

import com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.Dinheiro;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class CalculadoraXPService {

    private static final int XP_BASE = 10;
    private static final BigDecimal DIVISOR_PROPORCIONAL = BigDecimal.TEN;
    private static final int TETO_PROPORCIONAL = 50;
    private static final int BONUS_POR_SEMANA_DE_STREAK = 5;


    public int calcular ( Dinheiro valorAporte, int semanaDeStreak) {
       int xpProporcional = calcularXpProporcional (valorAporte);
       int xpStreak = semanaDeStreak * BONUS_POR_SEMANA_DE_STREAK;
       return XP_BASE + xpProporcional + xpStreak;
    }

    private int calcularXpProporcional (Dinheiro valorAporte) {
        BigDecimal proporcional = valorAporte.valor()
                .divide(DIVISOR_PROPORCIONAL, 0 , RoundingMode.DOWN);
        return  Math.min(proporcional.intValue(), TETO_PROPORCIONAL);
    }
}

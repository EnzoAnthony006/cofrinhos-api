package com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.gamificacao;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Streak {

    private int sequenciaAtual;
    private int melhorSequencia;
    private LocalDate dataUltimaAtividade;

    private Streak(int sequenciaAtual, int melhorSequencia, LocalDate dataUltimaAtividade) {
        this.sequenciaAtual = sequenciaAtual;
        this.melhorSequencia = melhorSequencia;
        this.dataUltimaAtividade = dataUltimaAtividade;
    }

    public static Streak iniciar() {
        return new Streak(0, 0, null);
    }
    public static Streak reconstruir (int sequenciaAtual, int melhorSequencia, LocalDate dataUltimaAtividade) {
       return new Streak(sequenciaAtual, melhorSequencia, dataUltimaAtividade);

    }

    public void registrarAtividade(LocalDate data) {
        if (dataUltimaAtividade == null || diasDesdeUltimaAtividade(data) <= 7) {
            sequenciaAtual++;
        } else {
            sequenciaAtual = 1;
        }
        dataUltimaAtividade = data;

        if (sequenciaAtual > melhorSequencia) {
            melhorSequencia = sequenciaAtual;
        }
    }
    private long diasDesdeUltimaAtividade(LocalDate data) {
        return ChronoUnit.DAYS.between(dataUltimaAtividade, data);

    }
    public int getSequenciaAtual() {
        return sequenciaAtual;
    }
    public int getMelhorSequencia() {
        return melhorSequencia;
    }
    public LocalDate getDataUltimaAtividade() {
        return dataUltimaAtividade;
    }
}

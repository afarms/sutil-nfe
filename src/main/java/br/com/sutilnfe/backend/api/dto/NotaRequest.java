package br.com.sutilnfe.backend.api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public record NotaRequest(String id,
                          String numero,
                          BigDecimal incremento,
                          Integer splitPj,
                          String descricao,
                          String emissao,
                          String competencia
                           ){

    public int getAno() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate data = LocalDate.parse(this.emissao, formatter);
        return data.getYear();
    }
}

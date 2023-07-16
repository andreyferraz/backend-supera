package br.com.banco.dtos;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransacoesPeriodoDTO {
    private BigDecimal valorTotalPorPeriodo;
    private List<TransferenciaContaDTO> transacoesPorPeriodo;
}

package br.com.banco.dtos;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SaldoTransacoesDTO {
   private BigDecimal saldoTotal; 
   private List<TransferenciaContaDTO> transacoes;
}

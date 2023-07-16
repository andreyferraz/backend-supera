package br.com.banco.dtos;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransferenciaContaDTO {
    private Integer idConta;
    private String nomeResponsavel;
    private Long idTransferencia;
    private Date dataTransferencia;
    private BigDecimal valor;
    private String tipo;
    private String nomeOperadorTransacao;
}

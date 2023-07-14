package br.com.banco.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "conta_transferencia_view")
public class TransferenciaContaView {
    @Id
    @Column(name = "id_conta")
    private Long idConta;

    @Column(name = "nome_responsavel")
    private String nomeResponsavel;

    @Column(name = "id")
    private Long idTransferencia;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "data_transferencia")
    private Date dataTransferencia;

    @Column(name = "valor")
    private BigDecimal valor;

    @Column(name = "tipo")
    private String tipo;

    @Column(name = "nome_operador_transacao")
    private String nomeOperadorTransacao;

}

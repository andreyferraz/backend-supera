package br.com.banco.controllers;

import java.math.BigDecimal;
import java.util.List;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.banco.dtos.SaldoTransacoesDTO;
import br.com.banco.dtos.TransacoesPeriodoDTO;
import br.com.banco.dtos.TransferenciaContaDTO;
import br.com.banco.exceptions.OperadorNotFoundException;
import br.com.banco.service.TransferenciaService;

@RestController
@RequestMapping("/transferencias")
public class TransferenciaController {
    @Autowired
    private final TransferenciaService transferenciaService;

    public TransferenciaController(TransferenciaService transferenciaService) {
        this.transferenciaService = transferenciaService;
    }

    @GetMapping
    public ResponseEntity<List<TransferenciaContaDTO>> obterTodasTransferencias() {
        List<TransferenciaContaDTO> transferencias = transferenciaService.obterTodasTransferencias();
        return ResponseEntity.ok(transferencias);
    }

    @GetMapping("/{idConta}")
    public ResponseEntity<?> obterTransferenciasPorConta(@PathVariable("idConta") Integer idConta) {
        List<TransferenciaContaDTO> transferencias = transferenciaService.obterTransferenciasPorConta(idConta);
        if (transferencias.isEmpty()) {
            return ResponseEntity.ok("Não há registros para essa conta");
        }
        return ResponseEntity.ok(transferencias);
    }

    @GetMapping("/saldo-total")
    public ResponseEntity<BigDecimal> obterSaldoTotal() {
        BigDecimal saldoTotal = transferenciaService.obterSaldoTotal();
        return ResponseEntity.ok(saldoTotal);
    }

    @GetMapping("/transacoes-por-operador/{nomeOperador}")
    public ResponseEntity<?> obterTransacoesPorOperador(@PathVariable String nomeOperador) {
        try {
            List<TransferenciaContaDTO> transacoesPorOperador = transferenciaService
                    .obterTransacoesPorOperador(nomeOperador);
            BigDecimal saldoTotalPorOperador = calcularSaldoTotal(transacoesPorOperador);

            SaldoTransacoesDTO saldoTransacoesDTO = new SaldoTransacoesDTO(saldoTotalPorOperador,
                    transacoesPorOperador);
            return ResponseEntity.ok(saldoTransacoesDTO);
        } catch (OperadorNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/transacoes-por-periodo")
    public ResponseEntity<TransacoesPeriodoDTO> obterTransacoesPorPeriodoEOperador(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date dataInicio,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date dataFim,
            @RequestParam(required = false) String nomeOperador
    ) {
        try {
            List<TransferenciaContaDTO> transacoesPorPeriodo = transferenciaService.obterTransacoesPorPeriodoEOperador(dataInicio, dataFim, nomeOperador);
            BigDecimal valorTotalPorPeriodo = transferenciaService.calcularValorTotalPorPeriodo(dataInicio, dataFim);

            TransacoesPeriodoDTO transacoesPeriodoDTO = new TransacoesPeriodoDTO(valorTotalPorPeriodo, transacoesPorPeriodo);
            return ResponseEntity.ok(transacoesPeriodoDTO);
        } catch (Exception e) {
            e.printStackTrace(); 
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    private BigDecimal calcularSaldoTotal(List<TransferenciaContaDTO> transacoesPorOperador) {
        BigDecimal saldoTotalPorOperador = BigDecimal.ZERO;
        for (TransferenciaContaDTO transferencia : transacoesPorOperador) {
            BigDecimal valor = transferencia.getValor();
            if ("DEPOSITO".equalsIgnoreCase(transferencia.getTipo())
                    || "TRANSFERENCIA".equalsIgnoreCase(transferencia.getTipo())) {
                saldoTotalPorOperador = saldoTotalPorOperador.add(valor);
            } else if ("SAQUE".equalsIgnoreCase(transferencia.getTipo())) {
                saldoTotalPorOperador = saldoTotalPorOperador.subtract(valor);
            }
        }
        return saldoTotalPorOperador;
    }
}

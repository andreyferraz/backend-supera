package br.com.banco.controllers;

import java.math.BigDecimal;
import java.util.List;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.banco.dtos.TransacoesPeriodoDTO;
import br.com.banco.dtos.TransferenciaContaDTO;
import br.com.banco.service.TransferenciaService;

@RestController
@CrossOrigin(origins = "*", methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT,
        RequestMethod.DELETE })

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

    @GetMapping("/transacoes-por-periodo")
    public ResponseEntity<?> obterTransacoesPorPeriodoEOperador(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date dataInicio,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date dataFim,
            @RequestParam(required = false) String nomeOperador) {
            if((dataInicio == null && dataFim != null) || (dataInicio != null && dataFim == null)){
                return ResponseEntity.badRequest().body("Ambas as datas de início e fim devem ser fornecidas ou nenhuma delas.");
            }    
        try {
            List<TransferenciaContaDTO> transacoesPorPeriodo = transferenciaService
                    .obterTransacoesPorPeriodoEOperador(dataInicio, dataFim, nomeOperador);
            BigDecimal valorTotalPorPeriodo = transferenciaService.calcularValorTotalPorPeriodo(dataInicio, dataFim);

            TransacoesPeriodoDTO transacoesPeriodoDTO = new TransacoesPeriodoDTO(valorTotalPorPeriodo,
                    transacoesPorPeriodo);
            return ResponseEntity.ok(transacoesPeriodoDTO);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

}

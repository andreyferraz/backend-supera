package br.com.banco.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import br.com.banco.dtos.TransferenciaContaDTO;
import br.com.banco.exceptions.OperadorNotFoundException;
import br.com.banco.model.TransferenciaContaView;
import br.com.banco.repository.TransferenciaContaViewRepository;

@Service
public class TransferenciaService {
    private final TransferenciaContaViewRepository transferenciaContaViewRepository;

    public TransferenciaService(TransferenciaContaViewRepository transferenciaContaViewRepository) {
        this.transferenciaContaViewRepository = transferenciaContaViewRepository;
    }

    public List<TransferenciaContaDTO> obterTodasTransferencias() {
        List<TransferenciaContaView> transferencias = transferenciaContaViewRepository.findAll();
        return transferencias.stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    public List<TransferenciaContaDTO> obterTransferenciasPorConta(Integer idConta) {
        List<TransferenciaContaView> transferencias = transferenciaContaViewRepository.findByIdConta(idConta);

        return transferencias.stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    public BigDecimal obterSaldoTotal() {
        List<TransferenciaContaView> transferencias = transferenciaContaViewRepository.findAll();
        BigDecimal saldoTotal = BigDecimal.ZERO;

        for (TransferenciaContaView transferencia : transferencias) {
            BigDecimal valor = transferencia.getValor();
            if ("DEPOSITO".equalsIgnoreCase(transferencia.getTipo())
                    || "TRANSFERENCIA".equalsIgnoreCase(transferencia.getTipo())) {
                saldoTotal = saldoTotal.add(valor);
            } else if ("SAQUE".equalsIgnoreCase(transferencia.getTipo())) {
                saldoTotal = saldoTotal.subtract(valor);
            }
        }
        return saldoTotal;
    }

    public List<TransferenciaContaDTO> obterTransacoesPorPeriodoEOperador(
            @RequestParam(required = false) Date dataInicio,
            @RequestParam(required = false) Date dataFim,
            @RequestParam(required = false) String nomeOperador) {
        List<String> tiposTransacao = Arrays.asList("SAQUE", "DEPOSITO", "TRANSFERENCIA");

        if(dataInicio == null && dataFim == null){
            TransferenciaContaView primeiraTransacao = transferenciaContaViewRepository.findFirstByOrderByDataTransferenciaAsc();
            if(primeiraTransacao != null){
                dataInicio = primeiraTransacao.getDataTransferencia();
            }
            dataFim = new Date();
        }

        if (dataInicio != null && dataFim != null) {
            if (nomeOperador != null && !nomeOperador.isEmpty()) {
                return transferenciaContaViewRepository
                        .findByDataTransferenciaBetweenAndTipoInAndNomeOperadorTransacaoIgnoreCase(
                                dataInicio, dataFim, tiposTransacao, nomeOperador).stream()
                               .map(this::converterParaDTO)
                               .collect(Collectors.toList());
            } else {
                return transferenciaContaViewRepository.findByDataTransferenciaBetweenAndTipoIn(
                        dataInicio, dataFim, tiposTransacao).stream()
                        .map(this::converterParaDTO)
                        .collect(Collectors.toList());
            }
        } else if (nomeOperador != null && !nomeOperador.isEmpty()) {
            return transferenciaContaViewRepository.findByNomeOperadorTransacaoIgnoreCase(nomeOperador).stream()
            .map(this::converterParaDTO)
            .collect(Collectors.toList());
        } else {
            return transferenciaContaViewRepository.findAll().stream()
            .map(this::converterParaDTO)
            .collect(Collectors.toList());
        }
    }

    public BigDecimal calcularValorTotalPorPeriodo(Date dataInicio, Date dataFim) {
        List<String> tiposTransacao = Arrays.asList("SAQUE", "DEPOSITO", "TRANSFERENCIA");
        List<TransferenciaContaView> transferencias = transferenciaContaViewRepository
                .findByDataTransferenciaBetweenAndTipoIn(dataInicio, dataFim, tiposTransacao);

        BigDecimal saldoTotalPorPeriodo = BigDecimal.ZERO;

        for (TransferenciaContaView transferencia : transferencias) {
            BigDecimal valor = transferencia.getValor();
            if ("DEPOSITO".equalsIgnoreCase(transferencia.getTipo())
                    || "TRANSFERENCIA".equalsIgnoreCase(transferencia.getTipo())) {
                saldoTotalPorPeriodo = saldoTotalPorPeriodo.add(valor);
            } else if ("SAQUE".equalsIgnoreCase(transferencia.getTipo())) {
                saldoTotalPorPeriodo = saldoTotalPorPeriodo.subtract(valor);
            }
        }
        return saldoTotalPorPeriodo;
    }

    private TransferenciaContaDTO converterParaDTO(TransferenciaContaView transferencia) {
        return TransferenciaContaDTO.builder()
                .idConta(transferencia.getIdConta())
                .nomeResponsavel(transferencia.getNomeResponsavel())
                .idTransferencia(transferencia.getIdTransferencia())
                .dataTransferencia(transferencia.getDataTransferencia())
                .valor(transferencia.getValor())
                .tipo(transferencia.getTipo())
                .nomeOperadorTransacao(transferencia.getNomeOperadorTransacao())
                .build();
    }

}

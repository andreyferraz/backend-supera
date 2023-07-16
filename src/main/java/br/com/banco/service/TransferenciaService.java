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

    public List<TransferenciaContaDTO> obterTransacoesPorOperador(String nomeOperador) {
        List<TransferenciaContaView> transferencias = transferenciaContaViewRepository
                .findByNomeOperadorTransacao(nomeOperador);

        BigDecimal saldoTotalPorOperador = BigDecimal.ZERO;
        List<TransferenciaContaDTO> transacoesPorOperador = new ArrayList<>();

        boolean operadorEncontrado = false;

        for (TransferenciaContaView transferencia : transferencias) {
            if (nomeOperador.equalsIgnoreCase(transferencia.getNomeOperadorTransacao())) {
                TransferenciaContaDTO transferenciaDTO = converterParaDTO(transferencia);
                transacoesPorOperador.add(transferenciaDTO);

                BigDecimal valor = transferencia.getValor();
                if ("DEPOSITO".equalsIgnoreCase(transferencia.getTipo())
                        || "TRANSFERENCIA".equalsIgnoreCase(transferencia.getTipo())) {
                    saldoTotalPorOperador = saldoTotalPorOperador.add(valor);
                } else if ("SAQUE".equalsIgnoreCase(transferencia.getTipo())) {
                    saldoTotalPorOperador = saldoTotalPorOperador.subtract(valor);
                }
                operadorEncontrado = true;
            }
        }
        if (!operadorEncontrado) {
            throw new OperadorNotFoundException("Não existem transações para esse operador");
        }
        return transacoesPorOperador;
    }

    public List<TransferenciaContaDTO> obterTransacoesPorPeriodoEOperador(
            @RequestParam(required = false) Date dataInicio,
            @RequestParam(required = false) Date dataFim,
            @RequestParam(required = false) String nomeOperador) {
        List<String> tiposTransacao = Arrays.asList("SAQUE", "DEPOSITO", "TRANSFERENCIA");
        List<TransferenciaContaView> transferencias;

        if (dataInicio != null && dataFim != null) {
            if (nomeOperador != null && !nomeOperador.isEmpty()) {
                transferencias = transferenciaContaViewRepository
                        .findByDataTransferenciaBetweenAndTipoInAndNomeOperadorTransacaoIgnoreCase(
                                dataInicio, dataFim, tiposTransacao, nomeOperador);
            } else {
                transferencias = transferenciaContaViewRepository.findByDataTransferenciaBetweenAndTipoIn(
                        dataInicio, dataFim, tiposTransacao);
            }
        } else if (nomeOperador != null && !nomeOperador.isEmpty()) {
            transferencias = transferenciaContaViewRepository.findByNomeOperadorTransacaoIgnoreCase(nomeOperador);
        } else {
            transferencias = transferenciaContaViewRepository.findAll();
        }

        return transferencias.stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
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

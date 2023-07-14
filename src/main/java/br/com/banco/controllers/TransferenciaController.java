package br.com.banco.controllers;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.catalina.connector.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.banco.dtos.TransferenciaContaDTO;
import br.com.banco.model.TransferenciaContaView;
import br.com.banco.repository.TransferenciaContaViewRepository;

@RestController
@RequestMapping("/transferencias")
public class TransferenciaController {
    private final TransferenciaContaViewRepository transferenciaRepository;

    public TransferenciaController(TransferenciaContaViewRepository transferenciaRepository){
        this.transferenciaRepository = transferenciaRepository;
    }

    @GetMapping("/{idConta}")
    public ResponseEntity<List<TransferenciaContaDTO>> obterTransferenciasPorConta(@PathVariable Long idConta){
        List<TransferenciaContaView> transferencias = transferenciaRepository.findByIdConta(idConta);

        List<TransferenciaContaDTO> transferenciasDTO = transferencias.stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());

            return ResponseEntity.ok(transferenciasDTO);
    }

    private TransferenciaContaDTO converterParaDTO(TransferenciaContaView transferencia){
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

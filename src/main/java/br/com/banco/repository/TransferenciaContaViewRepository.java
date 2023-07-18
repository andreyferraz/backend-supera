package br.com.banco.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import br.com.banco.model.TransferenciaContaView;

public interface TransferenciaContaViewRepository extends JpaRepository<TransferenciaContaView, Integer>{
    List<TransferenciaContaView> findByIdConta(@Param("idConta") Integer idConta);  

    List<TransferenciaContaView> findByDataTransferenciaBetweenAndTipoIn(
            Date dataInicio, Date dataFim, List<String> tiposTransacao
    );

    List<TransferenciaContaView> findByNomeOperadorTransacaoIgnoreCase(@Param("nomeOperador") String nomeOperador);

    List<TransferenciaContaView> findByDataTransferenciaBetweenAndTipoInAndNomeOperadorTransacaoIgnoreCase(
            Date dataInicio, Date dataFim, List<String> tiposTransacao, String nomeOperador
    );

    TransferenciaContaView findFirstByOrderByDataTransferenciaAsc();

}

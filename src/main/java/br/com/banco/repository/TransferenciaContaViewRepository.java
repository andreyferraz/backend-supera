package br.com.banco.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.banco.model.TransferenciaContaView;

public interface TransferenciaContaViewRepository extends JpaRepository<TransferenciaContaView, Long>{

    List<TransferenciaContaView> findByIdConta(Long idConta);
    
}

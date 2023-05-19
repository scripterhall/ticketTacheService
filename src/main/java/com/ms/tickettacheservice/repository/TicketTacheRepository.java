package com.ms.tickettacheservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ms.tickettacheservice.entity.TicketTache;

public interface TicketTacheRepository extends JpaRepository<TicketTache,Long> {
    
   
    List<TicketTache> findByTicketHistoireId(Long id);
    void deleteAllByTicketHistoireId(Long id);
    List<TicketTache> findByMembreId(Long id);
   
    
}

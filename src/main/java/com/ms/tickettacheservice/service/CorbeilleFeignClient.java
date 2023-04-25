package com.ms.tickettacheservice.service;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.ms.tickettacheservice.entity.TicketTache;

@FeignClient(name="corbeille-service")
public interface CorbeilleFeignClient {
    
    @PostMapping("/corbeilles")
    public TicketTache ajouterTicketTache(@RequestBody TicketTache ticketTache);

    @GetMapping("/corbeilles/membres/{id}")
    public List<TicketTache> getTicketTacheByMembreId(@PathVariable("id") Long id);


    
}

package com.ms.tickettacheservice.controller;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.ms.tickettacheservice.model.Sprint;
import com.ms.tickettacheservice.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;

import org.springframework.web.bind.annotation.*;

import com.ms.tickettacheservice.entity.TicketTache;
import com.ms.tickettacheservice.model.HistoireTicket;
import com.ms.tickettacheservice.model.Membre;
import com.ms.tickettacheservice.model.SprintBacklog;

import com.ms.tickettacheservice.service.HistoireTicketFeignClient;
import com.ms.tickettacheservice.service.MembreFeignClient;
import com.ms.tickettacheservice.service.SprintBacklogFeignClient;
import com.ms.tickettacheservice.service.TicketTacheService;

import jakarta.transaction.Transactional;

@RestController
@RequestMapping("ticket-taches")
public class TicketTacheController {

    @Autowired
    private TicketTacheService ticketTacheService;

    @Autowired
    private MembreFeignClient membreClient;

    @Autowired
    HistoireTicketFeignClient histoireTicketFeignClient;

    @Autowired
    SprintBacklogFeignClient sprintBacklogFeignClient;
    @Autowired
    private SprintFeignClient sprintFeignClient;

    @Autowired
    private CorbeilleFeignClient corbeilleFeignClient;

 

    @GetMapping
    public ResponseEntity<List<TicketTache>> getTicketsTacheByIdSprint(@RequestParam("sprintId") Long sprintId) {
        List<TicketTache> tts = this.ticketTacheService.findAll();
        List<TicketTache> filteredTts = new ArrayList<>();
        for (TicketTache tt : tts) {
            if (tt.getSprintBacklogId() != null) {
                tt.setSprintBacklog(this.sprintBacklogFeignClient.getSprintBacklogById(tt.getSprintBacklogId()));
                if (tt.getSprintBacklog().getSprintId().equals(sprintId)) {
                    Sprint sprint = this.sprintFeignClient.getSprintById(sprintId);
                    if (sprint.getEtat().equals("terminé")) {
                        this.ticketTacheService.modifierTicketTache(tt);
                    }
                    if (sprint.getEtat().equals("en attente")) {
                        this.ticketTacheService.modifierTicketTache(tt);
                    }
                    if (tt.getTicketHistoireId() != null)
                        tt.setHt(this.histoireTicketFeignClient.ticketHistoireById(tt.getTicketHistoireId()));
                    filteredTts.add(tt);
                }
            }
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(filteredTts);
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @MessageMapping("/topic/tache-socket")
    public ResponseEntity<TicketTache> ajouterTicketTache(@RequestBody TicketTache tt) {

        TicketTache tt_saved = ticketTacheService.ajouterTicketTache(tt);

        if (tt_saved == null) {
            return new ResponseEntity<>(tt_saved, HttpStatus.BAD_REQUEST);
        }

        if (tt.getSprintBacklogId() != null) {
            SprintBacklog sprintBacklog = this.sprintBacklogFeignClient.getSprintBacklogById(tt.getSprintBacklogId());
            tt_saved.setSprintBacklog(sprintBacklog);
        }
        HistoireTicket ht = this.histoireTicketFeignClient.ticketHistoireById(tt.getTicketHistoireId());
        tt_saved.setHt(ht);

        

        return new ResponseEntity<>(tt_saved, HttpStatus.CREATED);

    }

    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<TicketTache> getTicketTacheById(@PathVariable("id") Long id) {

        TicketTache tt = ticketTacheService.getTicketTacheById(id);

        if (tt == null) {
            return ResponseEntity.notFound().build();
        }

        SprintBacklog sprintBacklog = this.sprintBacklogFeignClient.getSprintBacklogById(tt.getSprintBacklogId());
        HistoireTicket ht = this.histoireTicketFeignClient.ticketHistoireById(tt.getTicketHistoireId());

        if (tt.getMembreId() != null) {
            Membre membre = this.membreClient.getMembreById(tt.getMembreId());
            tt.setMembre(membre);
        }

        tt.setHt(ht);
        tt.setSprintBacklog(sprintBacklog);

        return new ResponseEntity<>(tt, HttpStatus.OK);
    }

    @PutMapping("/{id-ticket}")
    @ResponseBody
    public ResponseEntity<TicketTache> prendreTicketTache(@RequestBody Membre membre,
            @PathVariable("id-ticket") Long id) {
        TicketTache ticketTache = ticketTacheService.prendreTicketTache(membre, id);
        SprintBacklog sprintBacklog =null ;
        if (ticketTache == null) {
            return ResponseEntity.notFound().build();
        }
        if(ticketTache.getSprintBacklogId()!=null)
        sprintBacklog = this.sprintBacklogFeignClient
        .getSprintBacklogById(ticketTache.getSprintBacklogId());
        HistoireTicket ht = this.histoireTicketFeignClient.ticketHistoireById(ticketTache.getTicketHistoireId());
        ticketTache.setSprintBacklog(sprintBacklog);
        ticketTache.setHt(ht);
        ticketTache.setMembre(membre);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(ticketTache);
    }

    @PutMapping
    @ResponseBody
    public ResponseEntity<TicketTache> modifierTicketTache(@RequestBody TicketTache ticketTache) {

        TicketTache ticketTacheSaved = ticketTacheService.modifierTicketTache(ticketTache);

        if (ticketTacheSaved == null)
            return ResponseEntity.notFound().build();

        HistoireTicket ht = this.histoireTicketFeignClient.ticketHistoireById(ticketTacheSaved.getTicketHistoireId());

        if (ticketTacheSaved.getSprintBacklogId() != null) {
            SprintBacklog sprintBacklog = this.sprintBacklogFeignClient
                    .getSprintBacklogById(ticketTacheSaved.getSprintBacklogId());
            ticketTacheSaved.setSprintBacklog(sprintBacklog);
        }

        ticketTacheSaved.setHt(ht);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(ticketTacheSaved);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void>  supprimerTicketTache(@PathVariable("id") Long id) {
        if(id!=null){
        TicketTache ticket = this.ticketTacheService.getTicketTacheById(id);
        ticket.setId(id);
        this.corbeilleFeignClient.ajouterTicketTache(ticket);
        boolean isDeleted = ticketTacheService.supprimerTicketTache(id);
        if (!isDeleted) 
            return ResponseEntity.notFound().build();
        return ResponseEntity.noContent().build();
    }else 
        return ResponseEntity.badRequest().body(null);
    }

    @DeleteMapping("/ticket-histoire/{id}")
    @Transactional
    public ResponseEntity<Void> supprimerTicketTacheParTicketHistoire(@PathVariable("id") Long id) {
       
            try{
            ticketTacheService.deleteAllByTicketHistoireId(id);
            return ResponseEntity.ok().build();
            }catch(Exception e){
                return ResponseEntity.internalServerError().body(null);
            }
        
    }

    @GetMapping("/ticket-histoire/{id-ticket-histoire}")
    public ResponseEntity<List<TicketTache>> getTicketsTacheByTicketHistoireId(@PathVariable("id-ticket-histoire") Long id){
        List<TicketTache> tts = this.ticketTacheService.getTicketTacheByHistoireTicketId(id);
        for (TicketTache tt : tts) {
            if (tt.getMembreId() != null) {
                tt.setMembre(this.membreClient.getMembreById(tt.getMembreId()));

            }
            tt.setHt(histoireTicketFeignClient.ticketHistoireById(id));
        }
        return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .body(tts);
    }

    @GetMapping("/membre/{id}")
    public ResponseEntity<List<TicketTache>> getTicketsTacheByMembreId(@PathVariable("id") Long id){

        if(id == null)
            return ResponseEntity.badRequest().body(null);
        else{
            try{
                List<TicketTache> ticketsTache = this.ticketTacheService.getTicketTacheByMembreId(id);
                ticketsTache.forEach(ticketTache ->{
                    if(ticketTache.getTicketHistoireId() !=null)
                        ticketTache.setHt(this.histoireTicketFeignClient.ticketHistoireById(ticketTache.getTicketHistoireId())); 
                    if(ticketTache.getSprintBacklogId()!=null)
                        ticketTache.setSprintBacklog(this.sprintBacklogFeignClient.getSprintBacklogById(ticketTache.getSprintBacklogId()));
                    ticketTache.setMembre(this.membreClient.getMembreById(id));
                });
                return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(ticketsTache);
            }catch(SQLException error){
                return ResponseEntity.internalServerError().body(null);
            }
        }
    }
  
}

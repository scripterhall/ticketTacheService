package com.ms.tickettacheservice.entity;

import java.util.Date;

import org.springframework.data.rest.core.config.Projection;

import com.ms.tickettacheservice.model.HistoireTicket;
import com.ms.tickettacheservice.model.Membre;
import com.ms.tickettacheservice.model.SprintBacklog;

@Projection(name="full-ticket-tache",types = TicketTache.class)
public interface TicketTacheProjection {
    
    public Long getId();
    
    public int getNbHeurs();
    public Long getMembreId();
    public Date getDateLancement();
    public Date getDateFin();
    public Long getSsprintBacklogId();
    public Long getTicketHistoireId();
    public String getEtat();//  en cours .. a verifier .. a faire .. terminer
    public Membre getMembre(); 
    public HistoireTicket getHt();
    public SprintBacklog getSprintBacklog();
}

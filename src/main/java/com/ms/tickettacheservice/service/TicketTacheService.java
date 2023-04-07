package com.ms.tickettacheservice.service;






import java.sql.SQLException;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import com.ms.tickettacheservice.entity.TicketTache;
import com.ms.tickettacheservice.model.Membre;
import com.ms.tickettacheservice.repository.TicketTacheRepository;

@Service
public class TicketTacheService {
    
    @Autowired
    private TicketTacheRepository ticketTacheRepository;

    public List<TicketTache> findAll() {
        return this.ticketTacheRepository.findAll();
    }
    public TicketTache ajouterTicketTache(TicketTache tt){
    
            List<TicketTache> tts = this.ticketTacheRepository.findAll();
            for(TicketTache ticket:tts){
                
                if(tt.equals(ticket)){
                    return null;
                }
                    
            }
            return this.ticketTacheRepository.save(tt);
        
       
    } 

    public TicketTache getTicketTacheById(Long id){
        return this.ticketTacheRepository.findById(id).get();
    }

    public TicketTache prendreTicketTache(Membre m,Long id){

        TicketTache tt = this.ticketTacheRepository.findById(id).get();
        Date aujourdhui = new Date();
        
        tt.setDateLancement(aujourdhui);
        Date hour = new Date(3600*(tt.getNbHeurs()*1000)); 
        Date dateFin = new Date(aujourdhui.getTime() + hour.getTime());
        
        tt.setDateFin(dateFin);
        tt.setMembreId(m.getId());
        tt.setMembre(m);
        return this.ticketTacheRepository.save(tt); 
    }

    public TicketTache modifierTicketTache(TicketTache tt){
        
        if(this.ticketTacheRepository.existsById(tt.getId()))
            return this.ticketTacheRepository.save(tt);
        return null;
    }

    public boolean supprimerTicketTache(Long id){
        Optional<TicketTache> ticketTache = this.ticketTacheRepository.findById(id);
        if (ticketTache.isPresent()) {
        try {
            this.ticketTacheRepository.deleteById(id);
            return true;
        } catch (EmptyResultDataAccessException ex) {
            ex.printStackTrace();
            return false;
        }
      }return false;
    }


    public List<TicketTache> getTicketTacheByHistoireTicketId(Long id){
        List<TicketTache> tts = this.ticketTacheRepository.findByTicketHistoireId(id);
        if(tts!=null)
            return tts;
        else
            return Collections.emptyList();
    }

    public void deleteAllByTicketHistoireId(Long id){
        this.ticketTacheRepository.deleteAllByTicketHistoireId(id);
    }
    //Possibilter de creation d un corbeille pour les ticket supprimer

    public List<TicketTache> getTicketTacheByMembreId(Long id) throws SQLException {
        if(id == null)
            return Collections.emptyList() ;
        else 
            return this.ticketTacheRepository.findByMembreId(id);
    }
}

package com.app.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.entities.administration.TStatuts;
import com.app.repositories.TStatutsRepository;


@Service
public class TStatutsService {
	TStatutsRepository tStatutsRepository;

	public TStatutsService(TStatutsRepository tStatutsRepository) {
        this.tStatutsRepository = tStatutsRepository;
    }
	
    public List<TStatuts> findAll() {
        return tStatutsRepository.findAll();
    }
    
    @Transactional
    public TStatuts findByStaCode(String staCode) {
        return tStatutsRepository.findByStaaCode(staCode)
            .orElseThrow(() -> new IllegalArgumentException("Statut non trouvé avec l'id : " + staCode));
    }
}

package com.app.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

//import com.app.entities.recouvre.Contra;
import com.app.repositories.ContraRepository;
import com.app.repositories.ContratSelectProjection;

@Service
public class ContraService {
	@Autowired
	private final ContraRepository contraRepository;
	
	public ContraService(ContraRepository contraRepository) {
		this.contraRepository = contraRepository;
	}
	
	public List<ContratSelectProjection> findContratDetailsNative() {
        return contraRepository.findContratDetailsNative();
    }

}

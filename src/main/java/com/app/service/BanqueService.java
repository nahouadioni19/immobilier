package com.app.service;

import org.springframework.data.domain.Pageable;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.app.entities.administration.Banque;
import com.app.repositories.BanqueRepository;

@Service
public class BanqueService {
	private final BanqueRepository banqueRepository;
	
	public BanqueService(BanqueRepository banqueRepository) {
        this.banqueRepository = banqueRepository;
    }
	
	
	public Banque findById(Long bancode) { 
		return	banqueRepository.findById(bancode).orElse(null); 
	}
	 
	
    public List<Banque> findAll() {
        return banqueRepository.findAll();
    }
    
    public Page<Banque> findAll(Pageable pageable) {
        return banqueRepository.findAll(pageable);
    }
    
    public Banque save(Banque banque) {       

        Banque saved = banqueRepository.save(banque);
     //   System.out.println("Requerant sauvegardé avec id=" + saved.getReqId());
        return saved;
    }
    
    public List<Banque> search(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return banqueRepository.findAll();
        }
        return banqueRepository.search(keyword);
    }
    
    public void deleteById(Long bancode) {
    	banqueRepository.deleteById(bancode);
    }
    
    public Page<Banque> search(String keyword, int page, int size) {
        return banqueRepository.findByBansigleContainingIgnoreCaseOrBanlibelleContainingIgnoreCase(keyword, keyword, PageRequest.of(page, size));
    }


}

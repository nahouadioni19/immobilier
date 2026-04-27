package com.app.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.app.entities.administration.Site;
import com.app.repositories.SiteRepository;

@Service
public class SiteService {
	private final SiteRepository siteRepository;
	
	public SiteService(SiteRepository siteRepository) {
		this.siteRepository = siteRepository;
	}
	
	public Site findById(String sitCode) {
        return siteRepository.findById(sitCode).orElse(null);
    }
	
    public List<Site> findAll() {
        return siteRepository.findAll();
    }
    
}

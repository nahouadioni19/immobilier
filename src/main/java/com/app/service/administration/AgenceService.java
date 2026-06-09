package com.app.service.administration;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.dto.AgenceDTO;
import com.app.dto.BailleurDTO;
import com.app.entities.administration.Agence;
import com.app.entities.recouvre.Bailleur;
import com.app.entities.referentiel.Profession;
import com.app.repositories.administration.AgenceRepository;
import com.app.service.base.BaseService;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class AgenceService extends BaseService<Agence> {

    private final AgenceRepository repo;

    @Override
    public JpaRepository<Agence, Integer> getRepository() {
        return repo;
    }

    public Agence findById(Integer id) {
        return repo.findById(id).orElse(null);
    }

    public Page<Agence> findAll(Pageable pageable) {
        return repo.findAll(pageable);
    }
    
    /*public Page<Agence> search(String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return repo.findAll(pageable);
        }
        return repo.search(keyword.trim(), pageable);
    }*/
    
    public Page<AgenceDTO> search(String keyword, Pageable pageable) {
    	
        Page<Agence> page;

        if (keyword == null || keyword.trim().isEmpty()) {
            page = repo.findAll( pageable);
        } else {
            page = repo.search(keyword.trim(), pageable);
        }

        return page.map(this::toDTO);
    }

    private AgenceDTO toDTO(Agence b) {
    	AgenceDTO dto = new AgenceDTO();

        dto.setId(b.getId());
        dto.setCode(b.getCode());
        dto.setNom(b.getNom());
        dto.setTelephone(b.getTelephone());

        return dto;
    }
}

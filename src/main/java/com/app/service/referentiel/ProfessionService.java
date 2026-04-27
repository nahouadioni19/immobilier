package com.app.service.referentiel;

import static com.app.utils.Constants.PROFESSION_CACHE;

import java.util.List;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.entities.administration.Banque;
import com.app.entities.recouvre.Bailleur;
import com.app.entities.referentiel.Pays;
import com.app.entities.referentiel.Profession;
//import com.app.entities.referentiel.Profession;
import com.app.repositories.referentiel.PaysRepository;
import com.app.repositories.referentiel.ProfessionRepository;
import com.app.service.base.BaseService;
import com.app.service.common.CacheUtils;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
@CacheConfig(cacheNames = { PROFESSION_CACHE })
public class ProfessionService extends BaseService<Profession> {

    private final ProfessionRepository repo;
    private final CacheUtils cacheUtils;

    @Override
    public JpaRepository<Profession, Integer> getRepository() {
        return repo;
    }

    @Cacheable(cacheNames = PROFESSION_CACHE)
    @Override
    public List<Profession> findAll() {
        return repo.findAll();
    }

    @Override
    public void afterUpdate(Profession entity) {
        super.afterUpdate(entity);
        clearCache(PROFESSION_CACHE);
    }

    @Override
    public void afterSave(Profession entity) {
        super.afterSave(entity);
        clearCache(PROFESSION_CACHE);
    }

    @Override
    protected CacheUtils getCacheUtils() {
        return this.cacheUtils;
    }
    
    public Profession saveProf(Profession profession) {
        return repo.save(profession);
    }
    
    public Page<Profession> findAll(Pageable pageable) {
        return repo.findAll(pageable);
    }
    
    public void deleteById(Integer id) {
    	repo.deleteById(id);
    }
    
    public Profession findById(Integer id) { 
		return	repo.findById(id).orElse(null); 
	}
    
    public Page<Profession> search(String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return repo.findAll(pageable);
        }
        return repo.search(keyword.trim(), pageable);
    }

}

package com.app.service.administration;

import static com.app.utils.Constants.TYPE_ROLE_CACHE;

import java.util.List;
import java.util.Optional;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.entities.administration.Typerole;
import com.app.entities.referentiel.Profession;
import com.app.repositories.administration.TyperoleRepository;
import com.app.service.base.BaseService;
import com.app.service.common.CacheUtils;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
@CacheConfig(cacheNames = { TYPE_ROLE_CACHE })
public class TyperoleService extends BaseService<Typerole> {

    private final TyperoleRepository repo;
    private final CacheUtils cacheUtils;

    @Override
    public JpaRepository<Typerole, Integer> getRepository() {
        return repo;
    }

    @Override
    public Optional<Typerole> findByCode(String code) {
        return repo.findByCode(code);
    }

    @Cacheable(cacheNames = TYPE_ROLE_CACHE)
    public List<Typerole> findAllTypeRole() {
        return repo.findAllTypeRole();
    }

    @Override
    public void afterUpdate(Typerole entity) {
        super.afterUpdate(entity);
        clearCache(TYPE_ROLE_CACHE); 
    }

    @Override
    public void afterSave(Typerole entity) {
        super.afterSave(entity);
        clearCache(TYPE_ROLE_CACHE); 
    }

    @Override 
    protected CacheUtils getCacheUtils(){
        return this.cacheUtils;
    }

    @Cacheable(cacheNames = TYPE_ROLE_CACHE)
    @Override
    public Optional<Typerole> findById(int id) {
        return super.findById(id);
    }
    
    public Page<Typerole> findAll(Pageable pageable) {
        return repo.findAll(pageable);
    }
    
    public Typerole saveTyperole(Typerole typerole) {
        return repo.save(typerole);
    }

}

package com.app.service.referentiel;

import static com.app.utils.Constants.PAYS_CACHE;

import java.util.List;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.entities.referentiel.Pays;
import com.app.repositories.referentiel.PaysRepository;
import com.app.service.base.BaseService;
import com.app.service.common.CacheUtils;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
@CacheConfig(cacheNames = { PAYS_CACHE })
public class PaysService extends BaseService<Pays> {

    private final PaysRepository repo;
    private final CacheUtils cacheUtils;

    @Override
    public JpaRepository<Pays, Integer> getRepository() {
        return repo;
    }

    @Cacheable(cacheNames = PAYS_CACHE)
    @Override
    public List<Pays> findAll() {
        return repo.findAll();
    }

    @Override
    public void afterUpdate(Pays entity) {
        super.afterUpdate(entity);
        clearCache(PAYS_CACHE);
    }

    @Override
    public void afterSave(Pays entity) {
        super.afterSave(entity);
        clearCache(PAYS_CACHE);
    }

    @Override
    protected CacheUtils getCacheUtils() {
        return this.cacheUtils;
    }

}

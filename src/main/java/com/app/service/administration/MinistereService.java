package com.app.service.administration;

import static com.app.utils.Constants.MINISTERE_CACHE;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

import com.app.entities.administration.Ministere;
import com.app.repositories.administration.MinistereRepository;
import com.app.security.UserPrincipal;
import com.app.service.base.BaseService;
import com.app.service.common.CacheUtils;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
@CacheConfig(cacheNames = { MINISTERE_CACHE })
public class MinistereService extends BaseService<Ministere> {

    private final MinistereRepository repo;
    private final CacheUtils cacheUtils;

    @Override
    public JpaRepository<Ministere, Integer> getRepository() {
        return repo;
    }

    @Override
    public Optional<Ministere> findByCode(String code) {
        return repo.findByCode(code);
    }
    
	
	/*
	 * @Cacheable(cacheNames = MINISTERE_CACHE) public List<Ministere>
	 * findAllActive() { return repo.findAllActive(); }
	 */
	  
	  @Override
	  
	  @Cacheable(cacheNames = MINISTERE_CACHE) public Optional<Ministere>
	  findById(int id) { return super.findById(id); }
	 

    public List<Ministere> findByTerm(String term) {
        return repo.findByTerm(term);
    }

    public Ministere getMinistereCourantSelected(Map<String, String> params, UserPrincipal acteurConnected) {
        Ministere minCourant = null;

        if (acteurConnected.isActeurMinistere()) {
            minCourant = acteurConnected.getAssignationCourant().getRole().getMinistere();
        } else {
            int idMin = params != null ? Integer.valueOf(params.getOrDefault("choix", "0")) : 0;
            var findMinistere = findById(idMin);
            minCourant = findMinistere.isPresent() ? findMinistere.get() : null;
        }

        return minCourant;
    }

	/*
	 * public void setList(Model model) { model.addAttribute("listMinisteres",
	 * findAllActive()); }
	 */

    @Override
    public void afterUpdate(Ministere entity) {
        super.afterUpdate(entity);
        clearCache(MINISTERE_CACHE);
    }

    @Override
    public void afterSave(Ministere entity) {
        super.afterSave(entity);
        clearCache(MINISTERE_CACHE);
    }

    @Override
    protected CacheUtils getCacheUtils() {
        return this.cacheUtils;
    }
}

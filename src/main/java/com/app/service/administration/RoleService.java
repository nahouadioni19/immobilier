package com.app.service.administration;

import static com.app.utils.Constants.ROLE_CACHE;

import java.util.List;
import java.util.Optional;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import lombok.RequiredArgsConstructor;

import com.app.dto.RoleDTO;
import com.app.entities.administration.Banque;
import com.app.entities.administration.Role;
import com.app.entities.administration.Typerole;
import com.app.repositories.administration.RoleRepository;
import com.app.repositories.base.BaseRepository;
import com.app.service.base.BaseService;
import com.app.service.common.CacheUtils;
@Service
@Transactional
@RequiredArgsConstructor
@CacheConfig(cacheNames = { ROLE_CACHE })
public class RoleService extends BaseService<Role> {

    private final RoleRepository repo;
    private final CacheUtils cacheUtils;

    @Override
    public List<Role> findAll() {
        return repo.findAll();
    }

    @Cacheable(cacheNames = ROLE_CACHE)
    public List<Role> findAllRole() {
        return repo.findAllRole();
    }

    @Override
    public void afterUpdate(Role entity) {
        super.afterUpdate(entity);
        clearCache(ROLE_CACHE);
    }

    @Override
    public void afterSave(Role entity) {
        super.afterSave(entity);
        clearCache(ROLE_CACHE);
    }

    @Override
    protected CacheUtils getCacheUtils() {
        return this.cacheUtils;
    }

    @Override
    public BaseRepository<Role, Integer> getRepository() {
        return repo;
    }

    @Override
    public Optional<Role> findByCode(String code) {
        return repo.findByCode(code);
    }

    @Cacheable(cacheNames = ROLE_CACHE)
    @Override
    public Optional<Role> findById(int id) {
        return super.findById(id);
    }

    public Role findByUsernameAndTypeRole(String username, String typeRoleCode) {
        List<Role> list = repo.findByUsernameAndTypeRole(username, typeRoleCode);
        return list.isEmpty() ? null : list.get(0);
    }

    public List<Role> findByTypeRoleCodeAndMinistereId(String typeRoleCode, int idMinistere) {
        return repo.findByTypeRoleCodeAndMinistereId(typeRoleCode, idMinistere);
    }

    public List<Role> findByTerm(String term) {
        return repo.findByTerm(term);
    }

    public int findNextSequenceCode(String partCode) {
        return repo.findNextSequenceCode(partCode, (partCode.length() + 1));
    }

    public List<Role> findAllNotUsed() {
        return repo.findAllNotUsed();
    }

    public List<Role> findAllNotUsedWithAllRole() {
        return repo.findAllNotUsedWithAllRole();
    }
    
    public Page<Role> findAll(Pageable pageable) {
        return repo.findAll(pageable);
    }
    
    @Transactional(readOnly = true)
    public Optional<Role> findByIdWithAssignations(int id) {
        return repo.findByIdWithAssignations(id);
    }
    
    @Transactional(readOnly = true)
    public List<RoleDTO> findAllLight() {
        return repo.findAll()
                   .stream()
                   .map(r -> new RoleDTO(r.getId(), r.getCode(), r.getLibelle()))
                   .toList();
    }
    
    public Role saverole(Role role) {
        return repo.save(role);
    }


}


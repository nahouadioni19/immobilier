package com.app.service.base;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.exceptions.CustomException;
import com.app.security.UserPrincipal;
import com.app.entities.BaseEntity;
import com.app.entities.administration.Agence;
import com.app.entities.administration.Utilisateur;
import com.app.service.common.CacheUtils;
import com.app.utils.Constants;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public abstract class BaseService<T> {

    public abstract JpaRepository<T, Integer> getRepository();

    public Optional<T> findById(int id) {
        return getRepository().findById(id);
    }
 
    public Optional<T> findByCode(String code) {
        return Optional.empty();
    }

    public Optional<T> findByLibelle(String libelle) {
        return Optional.empty();
    }

    public List<T> findAll() {
        return getRepository().findAll();
    }

    public List<T> findAll(int page, int size) {
        return findPaginated(page, size, null, null).toList();
    }

    public Page<T> filterAll(int page, int size, String sortField, String sortDirection, String terms) {
        return null;
    }

    protected Sort getFilterSort(String sortField, String sortDirection) {
        Sort sort = null;
        if (!StringUtils.isBlank(sortField))
            sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending()
                    : Sort.by(sortField).descending();
        return sort;
    }

    public Page<T> findPaginated(int page, int size, String sortField, String sortDirection) {
        Sort sort = getFilterSort(sortField, sortDirection);
        return getRepository().findAll(getPageable(page, size, sort));
    }

    public List<T> findAll(int page) {
        return this.findAll(page, Constants.NOMBRE_ELEMENTS_PAR_PAGE);
    }

    public Pageable getPageable(int page, int size, Sort sort) {
        if (sort == null)
            return PageRequest.of(page, size);
        else
            return PageRequest.of(page, size, sort);
    }

    public Pageable getDefaultPageable(int page) {
        return PageRequest.of(page, Constants.NOMBRE_ELEMENTS_PAR_PAGE);
    }

    public List<T> findByIds(Iterable<Integer> ids) {
        return getRepository().findAllById(ids);
    }

    public void beforeSave(T entity){
    }

    public void afterSave(T entity) {
    }

    public void save(T entity) {
        try {
            beforeSave(entity);
            getRepository().save(entity);
            afterSave(entity);
        } catch (Exception e) {
            throw new CustomException(e);
        }
    }

    public void saveAll(Iterable<T> entities) {
        try {
            entities.forEach(this::beforeSave);
            this.getRepository().saveAll(entities);
            entities.forEach(this::afterSave);
        } catch (Exception e) {
            throw new CustomException(e);
        }
    }

    public void updateAll(Iterable<T> entities) {
        try {
            entities.forEach(this::beforeUpdate);
            this.getRepository().saveAll(entities);
            entities.forEach(this::afterUpdate);
        } catch (Exception e) {
            throw new CustomException(e);
        }
    }

    public void updateWithLogs(Iterable<T> entities) {
        try {
            entities.forEach(this::beforeUpdateWithLog);
            this.getRepository().saveAll(entities);
            entities.forEach(this::afterUpdateWithLog);
        } catch (Exception e) {
            throw new CustomException(e);
        }
    }

    public void beforeUpdateWithLog(T entity) {
    }

    public void afterUpdateWithLog(T entity) {
    }

    public void beforeUpdate(T entity) {
    }

    public void afterUpdate(T entity) {
    }
 
    protected CacheUtils getCacheUtils(){
        return null;
    }

    public void clearCache(String cacheName) { 
        getCacheUtils().clearCache(cacheName);
    } 

    public void update(T entity) {
        try {
            beforeUpdate(entity);
            getRepository().save(entity);
            afterUpdate(entity);
        } catch (Exception e) {
            throw new CustomException(e);
        }
    }

    public void beforeDelete(T entity) {
    }

    public void afterDelete(T entity) {
    }

    public void delete(T entity) {
        try {
            beforeDelete(entity);
            getRepository().delete(entity);
            afterDelete(entity);
        } catch (Exception e) {
            throw new CustomException(e);
        }
    }

    public void delete(int id) {
        T entity = findById(id).orElseThrow(() -> new IllegalArgumentException(
                this.getClass().getSimpleName() + " avec Id:" + id + " introuvable."));
        delete(entity);
    }

    public void log(Exception e) {
        log.error("Service::base: " + e.getMessage(), e);
    }

    public void logInfo(String msg) {
        log.error("Service::base: INFO:: " + msg);
    }

    public <E extends BaseEntity> List<E> parentCrud(List<E> listOldChildren, List<E> listNewChildren) {
        Map<Integer, E> mapOldChildren = new HashMap<>();

        if (listOldChildren != null) {
            mapOldChildren = listOldChildren.stream().collect(Collectors.toMap(BaseEntity::getId, item -> item));
        }

        return parentCrud(mapOldChildren, listNewChildren);
    }

    @SuppressWarnings("all")
    public <E extends BaseEntity> List<E> parentCrud(Map<Integer, E> mapOldChildren, List<E> listNewChildren) {
        List<E> newChildren = new ArrayList<>();

        if (listNewChildren != null) {
            E oldItem;
            int oldItemKey;

            for (E item : listNewChildren) {
                oldItemKey = item.getId();
                oldItem = mapOldChildren.get(oldItemKey);

                if (oldItem != null) {
                    mapOldChildren.remove(oldItemKey);
                }

                newChildren.add(item);
            }
        }

        for (Entry<Integer, E> oasEntry : mapOldChildren.entrySet()) {
            delete((T) oasEntry.getValue());
        }

        return newChildren;
    }
    
    protected Agence getCurrentAgence() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal() == null) {
            throw new RuntimeException("Non authentifié");
        }

        Object principal = auth.getPrincipal();

        if (principal instanceof UserPrincipal userPrincipal) {

            Utilisateur user = userPrincipal.getUtilisateur();

            if (user == null || user.getAgence() == null) {
                throw new RuntimeException("Agence non définie pour l'utilisateur connecté");
            }

            return user.getAgence();
        }

        throw new RuntimeException("Principal invalide : " + principal.getClass().getName());
    }
    
    protected Integer getCurrentAgenceId() {
        Agence agence = getCurrentAgence();
        return (agence != null) ? agence.getId() : null;
    }
    
    public void checkAgenceActive(Agence agence) {

        if (agence == null) {
            throw new SecurityException("Agence invalide");
        }

        if (agence.getBloque() != null && agence.getBloque()) {
            throw new SecurityException("Agence bloquée");
        }

        if (agence.getDateFinAbonnement() != null &&
            LocalDate.now().isAfter(agence.getDateFinAbonnement())) {
            throw new SecurityException("Abonnement expiré");
        }
    }
    
}


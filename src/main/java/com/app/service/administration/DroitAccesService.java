package com.app.service.administration;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.entities.administration.DroitAcces;
import com.app.entities.administration.Typerole;
import com.app.repositories.administration.DroitAccesRepository;
import com.app.service.base.BaseService;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class DroitAccesService extends BaseService<DroitAcces> {

    private final DroitAccesRepository repo;

    @Override
    public JpaRepository<DroitAcces, Integer> getRepository() {
        return repo;
    }

    public List<DroitAcces> findByTypeRole(Typerole typeRole) {
        return repo.findByTypeRole(typeRole);
    }

}

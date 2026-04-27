package com.app.service.administration;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.entities.administration.Agence;
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
}

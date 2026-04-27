package com.app.service.administration;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.entities.administration.Parametre;
import com.app.repositories.administration.ParametreRepository;
import com.app.service.base.BaseService;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ParametreService extends BaseService<Parametre> {

    private final ParametreRepository repo;

    @Override
    public JpaRepository<Parametre, Integer> getRepository() {
        return repo;
    }

    @Override
    public Optional<Parametre> findByCode(String code) {
        return repo.findByCode(code);
    }

    public List<Parametre> findByTypeCode(String code) {
        return repo.findByTypeCode(code);
    }

}


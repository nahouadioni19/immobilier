package com.app.service.referentiel;

import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import com.app.entities.referentiel.Statut;
import com.app.repositories.referentiel.StatutRepository;
import com.app.service.base.BaseService;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class StatutService extends BaseService<Statut> {
    
    private final StatutRepository repo;

    @Override
    public JpaRepository<Statut, Integer> getRepository()
    {
        return repo;
    }
}


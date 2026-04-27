package com.app.service.referentiel;
import java.util.List;
import java.util.Optional;

import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import com.app.entities.referentiel.Exercice;
import com.app.repositories.referentiel.ExerciceRepository;
import com.app.service.base.BaseService;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ExerciceService extends BaseService<Exercice>{

    private final ExerciceRepository repo;

    @Override
    public JpaRepository<Exercice, Integer> getRepository()
    {
        return repo;
    }

    public int findMax() {
        return repo.findMax();
    }

    public List<Exercice> findAllAnneeDesc() {
        return repo.findByOrderByAnneeDesc();
    }

    public List<Exercice> findByStatutCode(String statut) {
        return repo.findByStatutCode(statut);
    }

    public Optional<Exercice> findByAnnee(int annee) {
        return repo.findByAnnee(annee);
    } 

    public long count() {
        return repo.count();
    }

    
}


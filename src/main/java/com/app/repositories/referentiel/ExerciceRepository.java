package com.app.repositories.referentiel;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.app.entities.referentiel.Exercice;

public interface ExerciceRepository extends JpaRepository<Exercice, Integer> {
    
    @Query("select coalesce(max(annee), 0) from Exercice")
    public int findMax();

    public List<Exercice> findByOrderByAnneeDesc();

    @Query("select e from Exercice e join e.statut s where s.code = :statutCode order by e.annee desc ")
    public List<Exercice> findByStatutCode(@Param("statutCode") String statutCode);

    public Optional<Exercice> findByAnnee(int annee);
}

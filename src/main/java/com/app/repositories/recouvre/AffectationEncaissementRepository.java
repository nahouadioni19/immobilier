package com.app.repositories.recouvre;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.entities.recouvre.AffectationEncaissement;

public interface AffectationEncaissementRepository
        extends JpaRepository<AffectationEncaissement, Integer> {

    List<AffectationEncaissement>
        findByEncaisse_IdOrderByIdAsc(Integer encaisseId);

    boolean existsByEncaisse_IdAndLoyann_Id(
            Integer encaisseId, Integer loyannId);
}

package com.app.repositories.administration;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.entities.administration.DroitAcces;
import com.app.entities.administration.Typerole;


public interface DroitAccesRepository extends JpaRepository<DroitAcces, Integer> {

    public List<DroitAcces> findByTypeRole(Typerole typeRole);
}
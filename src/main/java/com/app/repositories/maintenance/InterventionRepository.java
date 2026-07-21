package com.app.repositories.maintenance;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.app.entities.maintenance.Intervention;

public interface InterventionRepository extends JpaRepository<Intervention, Integer>{
	
	Optional<Intervention> findByIdAndAgenceId(Integer id, Integer agenceId);
	
	//
	Page<Intervention> findByAgenceId(Integer agenceId, Pageable pageable);

}

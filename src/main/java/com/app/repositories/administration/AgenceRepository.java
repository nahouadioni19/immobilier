package com.app.repositories.administration;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.app.entities.administration.Agence;

public interface AgenceRepository extends JpaRepository<Agence, Integer>{

	@Query("""
		    SELECT a FROM Agence a
		    WHERE 
		        LOWER(a.nom) LIKE LOWER(CONCAT('%', :keyword, '%'))
		    ORDER BY a.nom ASC
		""")
		Page<Agence> search(@Param("keyword") String keyword, Pageable pageable);
	
	
}

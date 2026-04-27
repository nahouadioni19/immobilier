package com.app.repositories.referentiel;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.app.entities.referentiel.Profession;

public interface ProfessionRepository extends JpaRepository<Profession, Integer>{
	
	@Query("""
		    SELECT p FROM Profession p
		    WHERE 
		        LOWER(p.libelle) LIKE LOWER(CONCAT('%', :keyword, '%'))
		    ORDER BY p.libelle ASC
		""")
		Page<Profession> search(@Param("keyword") String keyword, Pageable pageable);    
}

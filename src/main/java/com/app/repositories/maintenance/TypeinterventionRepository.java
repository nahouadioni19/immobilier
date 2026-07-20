package com.app.repositories.maintenance;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.app.entities.maintenance.Prestataire;
import com.app.entities.maintenance.Typeintervention;

public interface TypeinterventionRepository extends JpaRepository<Typeintervention, Integer>{
	
	@Query("""
		    SELECT t FROM Typeintervention t
		    WHERE (
		        :keyword IS NULL OR :keyword = '' OR
		        LOWER(t.libelle) LIKE LOWER(CONCAT('%', :keyword, '%'))
		    )
		    ORDER BY t.libelle ASC
		""")
		Page<Typeintervention> search(@Param("keyword") String keyword,
		                      Pageable pageable);	
	
	///
	Page<Typeintervention> findAllByOrderByLibelleAsc(Pageable pageable);

}

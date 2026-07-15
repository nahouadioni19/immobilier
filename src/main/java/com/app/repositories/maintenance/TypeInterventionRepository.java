package com.app.repositories.maintenance;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.app.entities.maintenance.TypeIntervention;

public interface TypeInterventionRepository extends JpaRepository<TypeIntervention, Integer>{
	
	@Query("""
		    SELECT t FROM TypeIntervention t
		    WHERE (
		        :keyword IS NULL OR :keyword = '' OR
		        LOWER(t.libelle) LIKE LOWER(CONCAT('%', :keyword, '%'))
		    )
		    ORDER BY t.libelle ASC
		""")
		Page<TypeIntervention> search(@Param("keyword") String keyword,
		                      Pageable pageable);	
	
	///
	Page<TypeIntervention> findAllByOrderByLibelleAsc(Pageable pageable);

}

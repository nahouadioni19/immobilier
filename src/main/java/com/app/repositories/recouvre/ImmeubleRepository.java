package com.app.repositories.recouvre;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.app.dto.ImmeubleDTO;
import com.app.entities.recouvre.Bailleur;
import com.app.entities.recouvre.Immeuble;

public interface ImmeubleRepository extends JpaRepository<Immeuble, Integer>{

	@Query("""
		    SELECT DISTINCT i
		    FROM Immeuble i
		    LEFT JOIN FETCH i.appartements
		    WHERE i.id = :id
		    AND i.agence.id = :agenceId
		""")
		Optional<Immeuble> findByIdWithAppartements(
		        @Param("id") Integer id,
		        @Param("agenceId") Integer agenceId
		);
	
	//
	
	@Query("""
		    SELECT i FROM Immeuble i
		    LEFT JOIN i.bailleur b
		    WHERE (:keyword IS NULL OR :keyword = ''
		           OR LOWER(b.nom) LIKE LOWER(CONCAT('%', :keyword, '%'))
		           OR LOWER(b.prenom) LIKE LOWER(CONCAT('%', :keyword, '%'))
		           OR LOWER(i.nomImmeuble) LIKE LOWER(CONCAT('%', :keyword, '%'))
		    )
		    AND i.agence.id = :agenceId
		""")
		Page<Immeuble> search(@Param("keyword") String keyword, @Param("agenceId") Integer agenceId, Pageable pageable);
	//	
	@Query("""
		    SELECT new com.app.dto.ImmeubleDTO(
		        i.id,
		        i.nomImmeuble,
		        i.codeImmeuble,
		        b.nom,
		        b.prenom,
		        u.nom,
		        u.prenoms
		    )
		    FROM Immeuble i
		    LEFT JOIN i.bailleur b
		    LEFT JOIN i.utilisateur u
		    WHERE i.agence.id = :agenceId
		      AND (
		          :keyword IS NULL OR :keyword = ''
		          OR LOWER(b.nom) LIKE LOWER(CONCAT('%', :keyword, '%'))
		          OR LOWER(b.prenom) LIKE LOWER(CONCAT('%', :keyword, '%'))
		          OR LOWER(i.nomImmeuble) LIKE LOWER(CONCAT('%', :keyword, '%'))
		      )
		""")
		Page<ImmeubleDTO> searchDTO(@Param("keyword") String keyword, @Param("agenceId") Integer agenceId, Pageable pageable);
	
	// 
		@Query("""
			    SELECT new com.app.dto.ImmeubleDTO(
			        i.id,
			        i.nomImmeuble,
			        i.codeImmeuble,
			        b.nom,
			        b.prenom,
			        u.nom,
			        u.prenoms
			    )
			    FROM Immeuble i
			    LEFT JOIN i.bailleur b
			    LEFT JOIN i.utilisateur u
			    WHERE i.agence.id = :agenceId
			""")
			Page<ImmeubleDTO> findAllDTO(@Param("agenceId") Integer agenceId, Pageable pageable);
		
		//
		Page<Immeuble> findByAgenceId(Integer agenceId, Pageable pageable);

}

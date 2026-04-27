package com.app.repositories.recouvre;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.app.dto.ImmeubleDTO;
import com.app.entities.recouvre.Immeuble;
import com.app.entities.recouvre.Locataire;

public interface ImmeubleRepository extends JpaRepository<Immeuble, Integer>{

	@Query("SELECT i FROM Immeuble i LEFT JOIN FETCH i.appartements WHERE i.id = :id")
	Optional<Immeuble> findByIdWithAppartements(@Param("id") int id);
	
	//
	
	@Query("""
		    SELECT i FROM Immeuble i
		    LEFT JOIN i.bailleur b
		    WHERE (:keyword IS NULL OR :keyword = ''
		           OR LOWER(b.nom) LIKE LOWER(CONCAT('%', :keyword, '%'))
		           OR LOWER(b.prenom) LIKE LOWER(CONCAT('%', :keyword, '%'))
		           OR LOWER(i.nomImmeuble) LIKE LOWER(CONCAT('%', :keyword, '%'))
		    )
		""")
		Page<Immeuble> search(@Param("keyword") String keyword, Pageable pageable);
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
		    WHERE (:keyword IS NULL OR :keyword = ''
		           OR LOWER(b.nom) LIKE LOWER(CONCAT('%', :keyword, '%'))
		           OR LOWER(b.prenom) LIKE LOWER(CONCAT('%', :keyword, '%'))
		           OR LOWER(i.nomImmeuble) LIKE LOWER(CONCAT('%', :keyword, '%'))
		    )
		""")
		Page<ImmeubleDTO> searchDTO(@Param("keyword") String keyword, Pageable pageable);
	
	// ⚡ méthode pour tout récupérer en DTO (sans filtre)
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
    """)
    Page<ImmeubleDTO> findAllDTO(Pageable pageable);

}

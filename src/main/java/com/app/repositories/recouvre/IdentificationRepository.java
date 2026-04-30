package com.app.repositories.recouvre;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.app.dto.IdentificationProjection;
import com.app.entities.recouvre.Identification;

@Repository
public interface IdentificationRepository extends JpaRepository<Identification, Integer>{
//	
	@Query(value = """
		    SELECT i.idt AS id, 
		           i.ide_numero AS numero
		    FROM t_identification i
		    JOIN t_utilisateur u ON u.idt = i.utilisateur_id
		    WHERE u.agence_id = :agenceId
		      AND (:agentId IS NULL OR u.idt = :agentId)
		      AND NOT EXISTS (
		            SELECT 1
		            FROM t_encaisse e
		            WHERE e.identification_id = i.idt
		              AND e.agence_id = :agenceId
		              AND (:encaisseId IS NULL OR e.idt <> :encaisseId)
		      )
		    ORDER BY i.ide_numero
		""", nativeQuery = true)
		List<IdentificationProjection> findBailDetailsByAdmin(
		        @Param("agentId") Long agentId,
		        @Param("agenceId") Long agenceId,
		        @Param("encaisseId") Integer encaisseId
		);
	
	//
	
	@Query(value = """
		    SELECT i.idt AS id,
		           i.ide_numero AS numero
		    FROM t_identification i
		    JOIN t_utilisateur u ON u.idt = i.utilisateur_id
		    WHERE u.agence_id = :agenceId
		      AND i.agence_id = :agenceId
		      AND (:agentId IS NULL OR u.idt = :agentId)
		      AND NOT EXISTS (
		           SELECT 1
		           FROM t_encaisse e
		           WHERE e.identification_id = i.idt
		           AND (:encaisseId IS NULL OR e.idt <> :encaisseId)
		      )
		    ORDER BY i.ide_numero
		""", nativeQuery = true)
		List<IdentificationProjection> findBailDetailsByAdmin(
		        @Param("agenceId") Integer agenceId,
		        @Param("agentId") Long agentId,
		        @Param("encaisseId") Integer encaisseId
		);
	
	///
	@Query(value = """
		    SELECT i.idt AS id, i.ide_numero AS numero
		    FROM t_identification i
		    JOIN t_utilisateur u ON u.idt = i.utilisateur_id
		    WHERE u.idt = :utilisateurId
		    	AND i.agence_id = :agenceId
		    	AND u.agence_id = :agenceId
		    	AND i.idt NOT IN (
		            SELECT e.identification_id
		            FROM t_encaisse e
		            WHERE e.identification_id = i.idt
		              AND e.agence_id = :agenceId 
		      )
		    """, nativeQuery = true)
		List<IdentificationProjection> findBailDetailsByUtilisateur(
				@Param("utilisateurId") Integer utilisateurId,
				@Param("agenceId") Integer agenceId);	
	//	
	@Query(value = """
			SELECT i.idt AS id, 
		    i.ide_numero AS numero
		FROM t_identification i
		JOIN t_utilisateur u ON u.idt = i.utilisateur_id
		WHERE u.idt = :utilisateurId
		AND i.agence_id = :agenceId
		AND u.agence_id = :agenceId
		AND NOT EXISTS (
		     SELECT 1
		     FROM t_encaisse e
		     WHERE e.identification_id = i.idt
		     AND e.agence_id = :agenceId
		)
		""", nativeQuery = true)
	List<IdentificationProjection> findBailDetailsByUtilisateurNouveau(
			@Param("utilisateurId") Integer utilisateurId,
			@Param("agenceId") Integer agenceId);
	
	///
	@Query("""
		    SELECT i.id AS id, i.ideNumero AS numero
		    FROM Identification i
		    WHERE i.utilisateur.id = :utilisateurId
			  AND i.agence.id = :agenceId
		      AND i.id NOT IN (
			      SELECT e.identification.id FROM Encaisse e
			      WHERE e.identification.id = i.id
			      AND e.agence.id = :agenceId
			      
			      )
		""")
		List<IdentificationProjection> findAvailableIdentificationsByUtilisateur(
				@Param("utilisateurId") Integer utilisateurId,
				@Param("agenceId") Integer agenceId);
	//
	@Query(value = """
		    SELECT i.idt AS id,
		           i.ide_numero AS numero
		    FROM t_identification i
		    JOIN t_utilisateur u ON u.idt = i.utilisateur_id
		    WHERE u.agence_id = :agenceId
		      AND i.agence_id = :agenceId
		      AND (:agentId IS NULL OR u.idt = :agentId)
		      AND NOT EXISTS (
		            SELECT 1
		            FROM t_encaisse e
		            WHERE e.identification_id = i.idt
		              AND e.agence_id = :agenceId
		              AND (:encaisseId IS NULL OR e.idt <> :encaisseId)
		      )
		    ORDER BY i.ide_numero
		""", nativeQuery = true)
		List<IdentificationProjection> findIdentificationsDisponibles(
		        @Param("agenceId") Long agenceId,
		        @Param("agentId") Long agentId,
		        @Param("encaisseId") Integer encaisseId
		);
	
	//
		
		@Query(value = """
		    SELECT i.idt AS id,
		           i.ide_numero AS numero
		    FROM t_identification i
		    JOIN t_utilisateur u ON u.idt = i.utilisateur_id
		    WHERE u.agence_id = :agenceId
		      AND u.idt = :utilisateurId
		      AND NOT EXISTS (
		          SELECT 1
		          FROM t_encaisse e
		          WHERE e.identification_id = i.idt
		            AND (:encaisseId IS NULL OR e.idt <> :encaisseId)
		      )
		""", nativeQuery = true)
		List<IdentificationProjection> findAvailableIdentifications(
		    @Param("agenceId") Integer agenceId,
		    @Param("utilisateurId") Integer utilisateurId,
		    @Param("encaisseId") Integer encaisseId
		);

}

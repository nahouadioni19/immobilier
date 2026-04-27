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
		    WHERE i.idt NOT IN (
		            SELECT e.identification_id
		            FROM t_encaisse e
		      )
		     AND (:agentId IS NULL OR u.idt = :agentId)
		    """, nativeQuery = true)
		List<IdentificationProjection> findBailDetailsByAdminancien(@Param("agentId") Long agentId);	
	
	//
	@Query(value = """
			SELECT i.idt AS id,
		    i.ide_numero AS numero
		FROM t_identification i
		JOIN t_utilisateur u ON u.idt = i.utilisateur_id
		WHERE u.idt = :agentId
		AND NOT EXISTS (
		     SELECT 1
		     FROM t_encaisse e
		     WHERE e.identification_id = i.idt
		     AND (:encaisseId IS NULL OR e.idt <> :encaisseId)
		     )
		""", nativeQuery = true)
	List<IdentificationProjection> findBailDetailsByAdmin(@Param("agentId") Long agentId,
														  @Param("encaisseId") Integer encaisseId);
	
	///
	@Query(value = """
		    SELECT i.idt AS id, i.ide_numero AS numero
		    FROM t_identification i
		    JOIN t_utilisateur u ON u.idt = i.utilisateur_id
		    WHERE u.nom_utilisateur = :username
		    	AND i.idt NOT IN (
		            SELECT e.identification_id
		            FROM t_encaisse e
		      )
		    """, nativeQuery = true)
		List<IdentificationProjection> findBailDetailsByUtilisateur(@Param("username") String username);
	
	//
	
	@Query(value = """
			SELECT i.idt AS id, 
		    i.ide_numero AS numero
		FROM t_identification i
		JOIN t_utilisateur u ON u.idt = i.utilisateur_id
		WHERE u.nom_utilisateur = :username
		AND NOT EXISTS (
		     SELECT 1
		     FROM t_encaisse e
		     WHERE e.identification_id = i.idt
		)
		""", nativeQuery = true)
	List<IdentificationProjection> findBailDetailsByUtilisateurNouveau(@Param("username") String username);
	
	///
	@Query("""
		    SELECT i.id AS id, i.ideNumero AS numero
		    FROM Identification i
		    WHERE i.utilisateur.username = :username
		      AND i.id NOT IN (SELECT e.identification.id FROM Encaisse e)
		""")
		List<IdentificationProjection> findAvailableIdentificationsByUtilisateur(@Param("username") String username);
	//
	@Query(value = """
		    SELECT i.idt AS id, i.ide_numero AS numero
		    FROM t_identification i
		    JOIN t_utilisateur u ON u.idt = i.utilisateur_id
		    WHERE u.nom_utilisateur = :username
		      AND (
		            i.idt NOT IN (
		                SELECT e.identification_id
		                FROM t_encaisse e
		                WHERE (:encaisseId IS NULL OR e.idt <> :encaisseId)
		            )
		          )
		    """, nativeQuery = true)
		List<IdentificationProjection> findIdentificationsDisponibles(
		        @Param("username") String username,
		        @Param("encaisseId") Integer encaisseId);
	
	//
	@Query(value = """
		    SELECT i.idt AS id, i.ide_numero AS numero
		    FROM t_identification i
		    JOIN t_utilisateur u ON u.idt = i.utilisateur_id
		    WHERE u.nom_utilisateur = :username
		      AND NOT EXISTS (
		          SELECT 1
		          FROM t_encaisse e
		          WHERE e.identification_id = i.idt
		            AND (:encaisseId IS NULL OR e.idt <> :encaisseId)
		      )
		""", nativeQuery = true)
		List<IdentificationProjection> findAvailableIdentifications(
		    @Param("username") String username,
		    @Param("encaisseId") Integer encaisseId);
	
	/*@Query(value = """
		    SELECT i.idt AS id, i.ide_numero AS numero
		    FROM t_identification i
		    JOIN t_utilisateur u ON u.idt = i.utilisateur_id
		    LEFT JOIN t_encaisse e ON e.identification_id = i.idt
		    WHERE u.nom_utilisateur = :username
		      AND (
		            e.idt IS NULL           -- identifications non utilisées
		            OR i.idt = (
		                SELECT e2.identification_id
		                FROM t_encaisse e2
		                WHERE e2.idt = :encaisseId
		            )
		          )
		    ORDER BY i.ide_numero
		    """, nativeQuery = true)
		List<IdentificationProjection> findAvailableIdentifications(
		        @Param("username") String username,
		        @Param("encaisseId") Integer encaisseId  // null si create
		);*/
	
}

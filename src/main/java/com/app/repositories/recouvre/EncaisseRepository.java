package com.app.repositories.recouvre;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.app.entities.administration.Utilisateur;
import com.app.entities.recouvre.Carnet;
import com.app.entities.recouvre.Encaisse;
import com.app.repositories.EncaisseListDto;

@Repository
public interface EncaisseRepository extends JpaRepository<Encaisse, Integer>{

	List<Encaisse> findByUtilisateur(Utilisateur utilisateur);
	
	//
	@Query("SELECT r FROM Encaisse r WHERE r.utilisateur.id = :userId")
	List<Encaisse> findAllByUtilisateurId(@Param("userId") Long userId);
	
	//
	@Query(value = """
		    SELECT 
		        t.idt AS connumero,
		        l.nom AS locnom,
		        l.prenom AS locprenom,
		        l.telephone AS loctel,
		        l.email AS locemail,
		        t.utilite AS usglibelle,
		        t.loyer AS conloyer,
		        p.libelle AS bailibelle,p.num_appart AS numero, t.der_paye_date AS derniereDate
		    FROM t_bail t
		    JOIN t_locataire l ON l.idt = t.locataire_id
		    JOIN t_appartement p ON p.idt = t.appartement_id
		    """, nativeQuery = true)
	List<Encaisse> findByUtilisateurAndCarActiveTrue(Utilisateur utilisateur);
	//

	@Query(value = """
		    SELECT *
		    FROM t_encaisse e
		    WHERE e.statut=1
		    """,
		    countQuery = """
		    SELECT COUNT(*)
		    FROM t_encaisse e
		    WHERE e.statut=1
		    """,
		    nativeQuery = true)
		Page<Encaisse> findEncaissesDetailsByAdmin(Pageable pageable);
	//

	//pour une liste paginée par utilisateur connecté
	@Query(value = """
		    SELECT e.*
		    FROM t_encaisse e		    
		    JOIN t_utilisateur u ON u.idt = e.utilisateur_id
		    WHERE u.nom_utilisateur = :username AND e.statut=0
		    """,
		    countQuery = """
		    SELECT COUNT(*)
		    FROM t_encaisse e		    
		    JOIN t_utilisateur u ON u.idt = e.utilisateur_id
		    WHERE u.nom_utilisateur = :username AND e.statut=0
		    """,
		    nativeQuery = true)
		Page<Encaisse> findEncaissesDetailsByUtilisateur(@Param("username") String username, Pageable pageable);
	//
	@Query(
		    value = """
		        SELECT 
		            e.id as id,
		            e.encDate as encDate,
		            e.encMontant as encMontant,
		            e.encMode as encMode,
		            l.nom as locataireNom,
		            l.prenom as locatairePrenom,
		            a.numAppart as appartementNumero,
		            u.nom as utilisateurNom,
		            u.prenoms as utilisateurPrenoms
		        FROM Encaisse e
		        JOIN e.bail b
		        JOIN b.locataire l
		        JOIN b.appartement a
		        JOIN e.utilisateur u
		        WHERE u.username = :username
		        AND e.statut = 0
		        AND (
		          :keyword IS NULL OR :keyword = ''
		           OR LOWER(l.nom) LIKE LOWER(CONCAT('%', :keyword, '%'))
    	           OR LOWER(l.prenom) LIKE LOWER(CONCAT('%', :keyword, '%'))
		      )
		        """,
		    countQuery = """
		        SELECT COUNT(e)
		        FROM Encaisse e
		        JOIN e.utilisateur u
		        WHERE u.username = :username
		        AND e.statut = 0
		        """
		)
		Page<EncaisseListDto> findEncaissePageByUtilisateur(
				@Param("username") String username, 
				@Param("keyword") String keyword, 
				Pageable pageable);

	//
	@Query(
		    value = """
		        SELECT 
		            e.id as id,
		            e.encDate as encDate,
		            e.encMontant as encMontant,
		            e.encMode as encMode,
		            l.nom as locataireNom,
		            l.prenom as locatairePrenom,
		            a.numAppart as appartementNumero,
		            u.nom as utilisateurNom,
		            u.prenoms as utilisateurPrenoms,
		            u.id as utilisateurId
		        FROM Encaisse e
		        JOIN e.bail b
		        JOIN b.locataire l
		        JOIN b.appartement a
		        JOIN e.utilisateur u
		        WHERE e.statut = 1
		        AND (:agentId IS NULL OR u.id = :agentId)
			      AND (
			          :keyword IS NULL OR :keyword = ''
			           OR LOWER(l.nom) LIKE LOWER(CONCAT('%', :keyword, '%'))
	    	           OR LOWER(l.prenom) LIKE LOWER(CONCAT('%', :keyword, '%'))
			      )
		        """,
		    countQuery = """
		        SELECT COUNT(e)
		        FROM Encaisse e
		        WHERE e.statut = 1
		        """
		)
		Page<EncaisseListDto> findEncaissePageByAdmin(
				@Param("agentId") Long agentId,
		        @Param("keyword") String keyword,
		        Pageable pageable);
	
	//
	@Query("""
		    SELECT 
		        e.id as id,
		        e.encDate as encDate,
		        e.encMontant as encMontant,
		        e.encMode as encMode,
		        l.nom as locataireNom,
		        l.prenom as locatairePrenom,
		        a.numAppart as appartementNumero,
		        u.nom as utilisateurNom,
		        u.prenoms as utilisateurPrenoms,
		        u.id as utilisateurId
		    FROM Encaisse e
		    JOIN e.bail b
		    JOIN b.locataire l
		    JOIN b.appartement a
		    JOIN e.utilisateur u
		    WHERE (:agentId IS NULL OR u.id = :agentId)
		      AND (
		          :keyword IS NULL OR :keyword = ''
		           OR LOWER(l.nom) LIKE LOWER(CONCAT('%', :keyword, '%'))
    	           OR LOWER(l.prenom) LIKE LOWER(CONCAT('%', :keyword, '%'))
		      )
		""")
		Page<EncaisseListDto> findWithFilters(
		        @Param("agentId") Long agentId,
		        @Param("keyword") String keyword,
		        Pageable pageable);
	
	//
	//
    boolean existsByBailId(Integer bailId);
}

package com.app.repositories.recouvre;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.app.dto.BailDTO;
import com.app.entities.recouvre.Bail;
import com.app.enums.StatutBail;
import com.app.repositories.BailSelectProjection;

@Repository
public interface BailRepository extends JpaRepository<Bail, Integer> {

    /* ===== Version Liste simple ===== */
    @Query(value = """
        SELECT  t.idt AS id,
                l.nom AS locnom,
                l.prenom AS locprenom,
                l.telephone AS loctel,
                l.email AS locemail,
                t.utilite AS usglibelle,
                t.montant_loyer AS montantloyer,
                t.statut as statut,
                p.libelle AS bailibelle,
                p.num_appart AS numero, 
                t.der_paye_date AS derniereDatePaiement,
                t.date_fin AS dateDebut
        FROM t_bail t
        JOIN t_locataire l ON l.idt = t.locataire_id
        JOIN t_appartement p ON p.idt = t.appartement_id
        JOIN t_immeuble i ON i.idt = p.immeub_id
        LEFT JOIN t_utilisateur u ON u.idt = i.utilisateur_id
        WHERE (:agentId IS NULL OR u.idt = :agentId)
        AND (:keyword IS NULL OR :keyword = ''
		      OR LOWER(l.nom) LIKE LOWER(CONCAT('%', :keyword, '%'))
    	      OR LOWER(l.prenom) LIKE LOWER(CONCAT('%', :keyword, '%'))
		     )        
        """, nativeQuery = true)
    List<BailSelectProjection> findBailDetailsAdmin(@Param("agentId") Long agentId, 
    		@Param("keyword") String keyword);

    //
    @Query(value = """
        SELECT  t.idt AS id,
                l.nom AS locnom,
                l.prenom AS locprenom,
                l.telephone AS loctel,
                l.email AS locemail,
                t.utilite AS usglibelle,
                t.montant_loyer AS montantloyer,
                t.statut as statut,
                p.libelle AS bailibelle,
                p.num_appart AS numero, 
                t.der_paye_date AS derniereDatePaiement,
                t.date_fin AS dateDebut
        FROM t_bail t
        JOIN t_locataire l ON l.idt = t.locataire_id
        JOIN t_appartement p ON p.idt = t.appartement_id
        JOIN t_immeuble i ON i.idt = p.immeub_id
        JOIN t_utilisateur u ON u.idt = i.utilisateur_id
        WHERE u.nom_utilisateur = :username
        """, nativeQuery = true)
    List<BailSelectProjection> findBailDetailsByUtilisateur(@Param("username") String username);

    /* ===== Version Pagination ===== */
    
    @Query(value = """
        SELECT  SELECT  t.idt AS id,
                l.nom AS locnom,
                l.prenom AS locprenom,
                l.telephone AS loctel,
                l.email AS locemail,
                t.utilite AS usglibelle,
                t.montant_loyer AS montantloyer,
                t.statut as statut,
                p.libelle AS bailibelle,
                p.num_appart AS numero, 
                t.der_paye_date AS derniereDatePaiement,
                t.date_fin AS dateDebut
        FROM t_bail t
        JOIN t_locataire l ON l.idt = t.locataire_id
        JOIN t_appartement p ON p.idt = t.appartement_id
        JOIN t_immeuble i ON i.idt = p.immeub_id
        LEFT JOIN t_utilisateur u ON u.idt = i.utilisateur_id
        """,
        countQuery = "SELECT COUNT(*) FROM t_bail t",
        nativeQuery = true)
    Page<BailSelectProjection> findBailDetailsAdmin(Pageable pageable);
    
//**************************************************/
    @Query(value = """
        SELECT  t.idt AS connumero,
                l.nom AS locnom,
                l.prenom AS locprenom,
                l.telephone AS loctel,
                l.email AS locemail,
                t.utilite AS usglibelle,
                t.montant_loyer AS conloyer,
                t.statut as statut,
                p.libelle AS bailibelle,
                p.num_appart AS numero, 
                t.der_paye_date AS derniereDate
        FROM t_bail t
        JOIN t_locataire l ON l.idt = t.locataire_id
        JOIN t_appartement p ON p.idt = t.appartement_id
        JOIN t_immeuble i ON i.idt = p.immeub_id
        JOIN t_utilisateur u ON u.idt = i.utilisateur_id
        WHERE u.nom_utilisateur = :username
        """,
        countQuery = """
        SELECT COUNT(*)
        FROM t_bail t
        JOIN t_locataire l ON l.idt = t.locataire_id
        JOIN t_appartement p ON p.idt = t.appartement_id
        JOIN t_immeuble i ON i.idt = p.immeub_id
        JOIN t_utilisateur u ON u.idt = i.utilisateur_id
        WHERE u.nom_utilisateur = :username
        """,
        nativeQuery = true)
    Page<BailSelectProjection> findBailDetailsByUtilisateur(@Param("username") String username, Pageable pageable);

    // Méthodes classiques sur Bail
    long countByStatut(StatutBail statut);
    long countByStatutAndMontantLoyerGreaterThan(StatutBail statut, double montantLoyer);
    long countByStatutAndUtilite(StatutBail statut, String utilite);
    //
    @Query("SELECT b FROM Bail b JOIN FETCH b.locataire WHERE b.id = :id")
    Optional<Bail> findByIdWithLocataire(@Param("id") int id);
   // 
    
    @Query("SELECT b FROM Bail b " +
    	       "JOIN FETCH b.locataire " +
    	       "JOIN FETCH b.appartement " +
    	       "WHERE b.id = :id")
    	Optional<Bail> findByIdWithLocataireAndAppartement(@Param("id") Integer id);
   // 
    @Query("""
    	    SELECT new com.app.dto.BailDTO(
    	        b.id,
    	        b.total,
    	        b.statut,
    	        l.nom,
    	        l.prenom,
    	        a.numAppart,
    	        a.libelle
    	    )
    	    FROM Bail b
    	    LEFT JOIN b.locataire l
    	    LEFT JOIN b.appartement a
    	    WHERE (:keyword IS NULL OR :keyword = ''
    	           OR LOWER(l.nom) LIKE LOWER(CONCAT('%', :keyword, '%'))
    	           OR LOWER(l.prenom) LIKE LOWER(CONCAT('%', :keyword, '%'))
    	    )
    	""")
    	Page<BailDTO> search(@Param("keyword") String keyword, Pageable pageable);
    //
    @Query("""
            SELECT new com.app.dto.BailDTO(
    	        b.id,
    	        b.total,
    	        b.statut,
    	        l.nom,
    	        l.prenom,
    	        a.numAppart,
    	        a.libelle
    	    )
    	    FROM Bail b
    	    LEFT JOIN b.locataire l
    	    LEFT JOIN b.appartement a
        """)
        Page<BailDTO> findAllDTO(Pageable pageable);
    //
    @Query(value = """
            SELECT  t.idt AS id,
                    l.nom AS locnom,
                    l.prenom AS locprenom,
                    l.telephone AS loctel,
                    l.email AS locemail,
                    t.utilite AS usglibelle,
                    t.montant_loyer AS montantloyer,
                    t.statut as statut,
                    p.libelle AS bailibelle,
                    p.num_appart AS numero, 
                    t.der_paye_date AS derniereDatePaiement,
                    t.date_fin AS dateDebut
            FROM t_bail t
            JOIN t_locataire l ON l.idt = t.locataire_id
            JOIN t_appartement p ON p.idt = t.appartement_id
            JOIN t_immeuble i ON i.idt = p.immeub_id
            JOIN t_utilisateur u ON u.idt = i.utilisateur_id
            WHERE u.idt = :userId
            AND (:keyword IS NULL OR :keyword = ''
		      OR LOWER(l.nom) LIKE LOWER(CONCAT('%', :keyword, '%'))
    	      OR LOWER(l.prenom) LIKE LOWER(CONCAT('%', :keyword, '%'))
		     )
            """, nativeQuery = true)
    List<BailSelectProjection> findBailDetailsByUtilisateur(
    		@Param("userId") Integer userId,
    		@Param("keyword") String keyword);
    
    ////
    @Query(value = """
            SELECT  t.idt AS id,
                    l.nom AS locnom,
                    l.prenom AS locprenom,
                    l.telephone AS loctel,
                    l.email AS locemail,
                    t.utilite AS usglibelle,
                    t.montant_loyer AS montantloyer,
                    t.statut as statut,
                    p.libelle AS bailibelle,
                    p.num_appart AS numero, 
                    t.der_paye_date AS derniereDatePaiement,
                    t.date_fin AS dateDebut
            FROM t_bail t
            JOIN t_locataire l ON l.idt = t.locataire_id
            JOIN t_appartement p ON p.idt = t.appartement_id
            WHERE t.idt = :id
            """, nativeQuery = true)
    BailSelectProjection findBailById(@Param("id") Integer id);
    
    //
    List<Bail> findByStatut(StatutBail statut);
    
    //
	/*
	 * @Query(""" SELECT b FROM Bail b JOIN FETCH b.locataire WHERE b.statut =
	 * 'ACTIF' AND ( :search IS NULL OR :search = '' OR
	 * LOWER(CONCAT(b.locataire.nom, ' ', b.locataire.prenom)) LIKE
	 * LOWER(CONCAT('%', :search, '%')) ) """) Page<Bail>
	 * searchBaux(@Param("search") String search, Pageable pageable);
	 */
    		
    
    @Query("""
    	    SELECT b
    	    FROM Bail b
    	    JOIN b.locataire l
    	    WHERE (:search IS NULL OR :search = '' OR
    	           l.nom ILIKE CONCAT('%', :search, '%') OR
    	           l.prenom ILIKE CONCAT('%', :search, '%'))
    	""")
    	Page<Bail> searchBaux(@Param("search") String search, Pageable pageable);
    
    //
	/*
	 * @Query(""" SELECT b FROM Bail b JOIN b.locataire l WHERE (:search IS NULL OR
	 * LOWER(l.nom) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(l.prenom) LIKE
	 * LOWER(CONCAT('%', :search, '%'))) """)
	 */
    
    @Query("""
    	    SELECT b
    	    FROM Bail b
    	    JOIN b.locataire l
    	    WHERE (:search IS NULL OR :search = '' OR
    	           l.nom ILIKE CONCAT('%', :search, '%') OR
    	           l.prenom ILIKE CONCAT('%', :search, '%'))
    	""")
    	List<Bail> searchBauxNoPage(String search);

}
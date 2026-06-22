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
import com.app.enums.StatutAppartement;
import com.app.enums.StatutBail;
import com.app.repositories.BailSelectProjection;

@Repository
public interface BailRepository extends JpaRepository<Bail, Integer> {
	
	

    /* ================= LISTE SIMPLE ================= */
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
        AND t.agence_id = :agenceId
    """, nativeQuery = true)
    List<BailSelectProjection> findBailDetailsAdmin(
            @Param("agentId") Long agentId,
            @Param("agenceId") Integer agenceId,
            @Param("keyword") String keyword
    );

    /* ================= PAGINATION ================= */
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
        WHERE t.agence_id = :agenceId
    """,
    countQuery = """
        SELECT COUNT(*)
        FROM t_bail t
        WHERE t.agence_id = :agenceId
    """,
    nativeQuery = true)
    Page<BailSelectProjection> findBailDetailsAdmin(
            @Param("agenceId") Integer agenceId,
            Pageable pageable
    );

    /* ================= BY USER ================= */
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
        AND t.agence_id = :agenceId
    """,
    countQuery = """
        SELECT COUNT(*)
        FROM t_bail t
        JOIN t_immeuble i ON i.idt = t.immeub_id
        JOIN t_utilisateur u ON u.idt = i.utilisateur_id
        WHERE u.nom_utilisateur = :username
        AND t.agence_id = :agenceId
    """,
    nativeQuery = true)
    Page<BailSelectProjection> findBailDetailsByUtilisateur(
            @Param("username") String username,
            @Param("agenceId") Integer agenceId,
            Pageable pageable
    );

    /* ================= FETCH ================= */
    @Query("""
        SELECT b FROM Bail b
        JOIN FETCH b.locataire
        WHERE b.id = :id AND b.agence.id = :agenceId
    """)
    Optional<Bail> findByIdWithLocataire(
            @Param("id") Integer id,
            @Param("agenceId") Integer agenceId
    );

    @Query("""
        SELECT b FROM Bail b
        JOIN FETCH b.locataire
        JOIN FETCH b.appartement
        WHERE b.id = :id AND b.agence.id = :agenceId
    """)
    Optional<Bail> findByIdWithLocataireAndAppartement(
            @Param("id") Integer id,
            @Param("agenceId") Integer agenceId
    );

    /* ================= DTO ================= */
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
        AND b.agence.id = :agenceId
    """)
    Page<BailDTO> search(
            @Param("keyword") String keyword,
            @Param("agenceId") Integer agenceId,
            Pageable pageable
    );

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
        WHERE b.agence.id = :agenceId
    """)
    Page<BailDTO> findAllDTO(
            @Param("agenceId") Integer agenceId,
            Pageable pageable
    );

    /* ================= AUTRES ================= */
    List<Bail> findByStatut(StatutBail statut);

    long countByStatut(StatutBail statut);

    long countByStatutAndMontantLoyerGreaterThan(StatutBail statut, double montantLoyer);

    long countByStatutAndUtilite(StatutBail statut, String utilite);

	
	
	
	
	
	
    
    
    
    
    
    
    
    
    
	
	
	
	
	
	
	
	
    

    /* ===== Version Pagination ===== */
    
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
		     AND t.agence_id = :agenceId
            """, nativeQuery = true)
    List<BailSelectProjection> findBailDetailsByUtilisateur(
    		@Param("userId") Integer userId,
    		@Param("keyword") String keyword,
    		@Param("agenceId") Integer agenceId);
    
   
    
    ////////////////////////////////////////////////////////////////////////////////////////
    
 // ================= UTILISATEUR SIMPLE =================
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
        AND t.agence_id = :agenceId
    """, nativeQuery = true)
    List<BailSelectProjection> findBailDetailsByUtilisateur(
            @Param("username") String username,
            @Param("agenceId") Integer agenceId);


    // ================= UTILISATEUR PAGINATION =================
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
        AND t.agence_id = :agenceId
    """,
    countQuery = """
        SELECT COUNT(*)
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
        AND t.agence_id = :agenceId
    """,
    nativeQuery = true)
    Page<BailSelectProjection> findBailDetailsByUtilisateurPage(
            @Param("userId") Integer userId,
            @Param("keyword") String keyword,
            @Param("agenceId") Integer agenceId,
            Pageable pageable);


    // ================= FIND BY ID =================
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
        AND t.agence_id = :agenceId
    """, nativeQuery = true)
    BailSelectProjection findBailById(
            @Param("id") Integer id,
            @Param("agenceId") Integer agenceId);


    // ================= JPQL SEARCH =================
    @Query("""
        SELECT b
        FROM Bail b
        JOIN b.locataire l
        WHERE (:search IS NULL OR :search = '' OR
               LOWER(l.nom) LIKE LOWER(CONCAT('%', :search, '%')) OR
               LOWER(l.prenom) LIKE LOWER(CONCAT('%', :search, '%')))
        AND b.agence.id = :agenceId
    """)
    Page<Bail> searchBaux(
            @Param("search") String search,
            @Param("agenceId") Integer agenceId,
            Pageable pageable);


    @Query("""
        SELECT b
        FROM Bail b
        JOIN b.locataire l
        WHERE (:search IS NULL OR :search = '' OR
               LOWER(l.nom) LIKE LOWER(CONCAT('%', :search, '%')) OR
               LOWER(l.prenom) LIKE LOWER(CONCAT('%', :search, '%')))
        AND b.agence.id = :agenceId
    """)
    List<Bail> searchBauxNoPage(
            @Param("search") String search,
            @Param("agenceId") Integer agenceId);
    
    
    long countByAgenceIdAndStatut(
            Integer agenceId,
            StatutBail statut);
    
    //en retard
  /*  @Query("""
    	    SELECT COALESCE(SUM(b.total), 0)
    	    FROM Bail b
    	    WHERE b.agence.id = :agenceId
    	      AND b.statut = com.app.enums.StatutBail.EN_RETARD
    	""")
    	Long totalImpayes(@Param("agenceId") Integer agenceId);*/
    
    //RETARD
    @Query("""
    	    SELECT COUNT(DISTINCT e.bail.id)
    	    FROM Encaisse e
    	    WHERE e.agence.id = :agenceId
    	      AND e.encArriere > 0
    	""")
    	Long countBauxEnRetard(
    	        @Param("agenceId") Integer agenceId
    	);
    
    //22062026
    @Query(value = """
    	    SELECT COALESCE(
    	        SUM(
    	            GREATEST(
    	                (
    	                    (
    	                        EXTRACT(YEAR FROM CURRENT_DATE) * 12
    	                        + EXTRACT(MONTH FROM CURRENT_DATE)
    	                    )
    	                    -
    	                    (
    	                        EXTRACT(YEAR FROM COALESCE(derniere_date_paiement, date_debut)) * 12
    	                        + EXTRACT(MONTH FROM COALESCE(derniere_date_paiement, date_debut))
    	                    )
    	                ) * montant_loyer,
    	                0
    	            )
    	        ),
    	        0
    	    )
    	    FROM immonet.t_bail
    	    WHERE agence_id = :agenceId
    	      AND statut = 'ACTIF'
    	""", nativeQuery = true)
    	Long totalImpayes(@Param("agenceId") Integer agenceId);
    
    //
    @Query(value = """
    	    SELECT COUNT(*)
    	    FROM immonet.t_bail
    	    WHERE agence_id = :agenceId
    	      AND statut = 'ACTIF'
    	      AND (
    	          (
    	              EXTRACT(YEAR FROM CURRENT_DATE) * 12
    	              + EXTRACT(MONTH FROM CURRENT_DATE)
    	          )
    	          -
    	          (
    	              EXTRACT(YEAR FROM COALESCE(derniere_date_paiement, date_debut)) * 12
    	              + EXTRACT(MONTH FROM COALESCE(derniere_date_paiement, date_debut))
    	          )
    	      ) > 0
    	""", nativeQuery = true)
    	Long nombreBauxEnRetard(@Param("agenceId") Integer agenceId);
    

}
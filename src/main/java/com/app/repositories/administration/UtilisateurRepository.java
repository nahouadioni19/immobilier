package com.app.repositories.administration;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.app.entities.administration.Utilisateur;

public interface UtilisateurRepository extends JpaRepository<Utilisateur, Integer> {

    /* =========================================================
       Constante JPQL commune (OK Hibernate 6)
       ========================================================= */

    String QUERY_UTILISATEUR = """
        select u
        from Utilisateur u
        where lower(u.username) like lower(concat('%', :terms, '%'))
           or lower(u.matricule) like lower(concat('%', :terms, '%'))
           or lower(u.nom) like lower(concat('%', :terms, '%'))
           or lower(u.prenoms) like lower(concat('%', :terms, '%'))
           or lower(u.telephone) like lower(concat('%', :terms, '%'))
    """;

    /* =========================================================
       Méthodes dérivées
       ========================================================= */

    List<Utilisateur> findAllByOrderByNomAscPrenomsAscMatriculeAsc();

    Optional<Utilisateur> findByUsernameIgnoreCase(String username);

    Optional<Utilisateur> findByMatriculeIgnoreCase(String matricule);

    /* =========================================================
       Chargement avec assignations (EntityGraph recommandé)
       ========================================================= */

    @EntityGraph(attributePaths = {"assignations", "assignations.role"})
    Optional<Utilisateur> findByUsername(String username);

    @EntityGraph(attributePaths = {"assignations", "assignations.role"})
    Optional<Utilisateur> findUtilisateurWithAssignationsById(int id);

    /* =========================================================
       ⚠️ JOIN FETCH : UNIQUEMENT SANS Page
       ========================================================= */

    @Query("""
        select distinct u
        from Utilisateur u
        left join fetch u.assignations a
        left join fetch a.role
        where u.id = :id
    """)
    Optional<Utilisateur> findByIdWithAssignations(@Param("id") int id);

    /* =========================================================
       Filtres avec pagination (PAS de fetch join)
       ========================================================= */

    @Query("""
        select u
        from Utilisateur u
        where (
               lower(u.username) like lower(concat('%', :terms, '%'))
            or lower(u.matricule) like lower(concat('%', :terms, '%'))
            or lower(u.nom) like lower(concat('%', :terms, '%'))
            or lower(u.prenoms) like lower(concat('%', :terms, '%'))
            or lower(u.telephone) like lower(concat('%', :terms, '%'))
            or u in (
                select a.utilisateur
                from Assignation a
                join a.role r
                where r.typeRole.code <> 'SSADM'
                  and lower(r.libelle) like lower(concat('%', :terms, '%'))
            )
        )
        and u not in (
            select a.utilisateur
            from Assignation a
            join a.role r
            where r.typeRole.code = 'SSADM'
        )
    """)
    Page<Utilisateur> filterAllWithAllRole(
        @Param("terms") String terms,
        Pageable pageable
    );

    @Query(QUERY_UTILISATEUR + """
        or u in (
            select a.utilisateur
            from Assignation a
            join a.role r
            where lower(r.libelle) like lower(concat('%', :terms, '%'))
        )
    """)
    Page<Utilisateur> filterAll(
        @Param("terms") String terms,
        Pageable pageable
    );

    @Query(QUERY_UTILISATEUR)
    List<Utilisateur> findByTerm(@Param("terms") String terms);

    /* =========================================================
       Génération séquence métier (OK Hibernate 6)
       ========================================================= */

    @Query("""
        select coalesce(
            max( to_number( substring(u.matricule, 4, 5), '99999' ) ),
            0
        ) + 1
        from Utilisateur u
        where u.matricule like concat(:partCode, '%')
    """)
    int findNextSequenceCode(@Param("partCode") String partCode);

    /* =========================================================
       Utilisateurs par rôle (recouvrement)
       ========================================================= */

    @Query("""
        select distinct u
        from Utilisateur u
        join u.assignations a
        join a.role r
        where r.code = :code
    """)
    List<Utilisateur> findByUtilisateurRecouvrement(@Param("code") String code);
    
    //
    @Query("""
    	    SELECT u FROM Utilisateur u
    	    LEFT JOIN FETCH u.assignations a
    	    LEFT JOIN FETCH a.role
    	    WHERE LOWER(u.username) = LOWER(:username)
    	""")
    	Optional<Utilisateur> findByUsernameIgnoreCaseWithRoles(@Param("username") String username);
    
    //
    @Query("""
    	    SELECT DISTINCT u FROM Utilisateur u
    	    LEFT JOIN FETCH u.assignations a
    	    LEFT JOIN FETCH a.role
    	    WHERE u.username = :username
    	""")
    	Optional<Utilisateur> findUserWithRoles(@Param("username") String username);
}

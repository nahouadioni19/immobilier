package com.app.repositories.administration;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.entities.administration.Assignation;
import com.app.entities.administration.Utilisateur;

@Repository
public interface AssignationRepository extends JpaRepository<Assignation, Integer> {

    Optional<Assignation> findFirstByUtilisateurAndCourantTrue(Utilisateur utilisateur);

}


/*
 * package com.app.repositories.administration;
 * 
 * import java.util.List; import java.util.Optional;
 * 
 * import org.springframework.data.domain.Page; import
 * org.springframework.data.domain.Pageable; import
 * org.springframework.data.jpa.repository.JpaRepository; import
 * org.springframework.data.jpa.repository.Query; import
 * org.springframework.data.repository.query.Param;
 * 
 * import com.app.entities.administration.Assignation; import
 * com.app.entities.administration.Role; import
 * com.app.entities.administration.Utilisateur;
 * 
 * public interface AssignationRepository extends JpaRepository<Assignation,
 * Integer> {
 * 
 * public List<Assignation> findByUtilisateur(Utilisateur utilisateur);
 * 
 * public Optional<Assignation> findByUtilisateurAndRole(Utilisateur
 * utilisateur, Role role);
 * 
 * public List<Assignation> findByRole_TypeRoleCode(String code);
 * 
 * public List<Assignation> findByCourant(boolean actif);
 * 
 * @Query("select a from Assignation a where " +
 * " lower(a.utilisateur.username) like lower(concat('%',:terms,'%')) or lower(a.utilisateur.nom) like lower(concat('%',:terms,'%')) or "
 * +
 * " lower(a.utilisateur.prenoms) like lower(concat('%',:terms,'%')) or lower(a.role.libelle) like lower(concat('%',:terms,'%')) or "
 * + " lower(a.role.ministere.libelle) like lower(concat('%',:terms,'%')) ")
 * public Page<Assignation> filterAll(@Param("terms") String terms, Pageable
 * pageable);
 * 
 * }
 */
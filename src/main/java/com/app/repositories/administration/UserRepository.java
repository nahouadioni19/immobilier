package com.app.repositories.administration;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.app.entities.administration.Utilisateur;


@Repository
public interface UserRepository extends JpaRepository<Utilisateur, Integer> {

    // Méthode pour récupérer un utilisateur par son nom d'utilisateur
	Utilisateur findByUsername(String username);

    // Optionnel : vérifier si un username existe
    boolean existsByUsername(String username);
    
    //
    @Query("""
    	    SELECT DISTINCT u FROM Utilisateur u
    	    LEFT JOIN FETCH u.assignations a
    	    LEFT JOIN FETCH a.role
    	    WHERE u.username = :username
    	""")
    	Optional<Utilisateur> findUserWithRoles(@Param("username") String username);
}

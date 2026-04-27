
  package com.app.repositories;
  
  import java.util.Optional;
  
  import org.springframework.data.jpa.repository.JpaRepository; import
  org.springframework.data.jpa.repository.Query; import
  org.springframework.data.repository.query.Param; import
  org.springframework.stereotype.Repository;
  
  import com.app.entities.administration.Utilisateur;
  
  @Repository public interface UserutilisateurRepository extends
  JpaRepository<Utilisateur, Integer>{
  
  @Query("SELECT COUNT(u) > 0 FROM Utilisateur u WHERE u.username = :username")
  boolean existsByUsername(@Param("username") String username);
  
  // Optional<Utilisateur> findByUsername(String username);
  
  }
 
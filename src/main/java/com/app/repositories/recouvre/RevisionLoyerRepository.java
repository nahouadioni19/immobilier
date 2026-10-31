package com.app.repositories.recouvre;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.app.entities.recouvre.RevisionLoyer;

import org.springframework.data.domain.Pageable;

public interface RevisionLoyerRepository
        extends JpaRepository<RevisionLoyer, Integer> {

    List<RevisionLoyer> findByBail_IdOrderByDateEffetAsc(Integer bailId);

    Optional<RevisionLoyer> findFirstByBail_IdOrderByDateEffetAsc(
            Integer bailId);

    boolean existsByBail_IdAndDateEffet(
            Integer bailId, java.time.LocalDate dateEffet);

    @Query("""
        select r
        from RevisionLoyer r
        where r.bail.id = :bailId
          and r.dateEffet <= :date
        order by r.dateEffet desc
        """)
    List<RevisionLoyer> trouverRevisionsApplicables(
            @Param("bailId") Integer bailId,
            @Param("date") java.time.LocalDate date);
    
  
	@Query("""
	    SELECT r
	    FROM RevisionLoyer r
	    WHERE r.bail.id = :bailId
	      AND r.dateEffet <= :dateMois
	    ORDER BY r.dateEffet DESC
	    """)
	List<RevisionLoyer> trouverRevisionsApplicables(
	        @Param("bailId") Integer bailId,
	        @Param("dateMois") LocalDate dateMois,
	        Pageable pageable);

    
	List<RevisionLoyer> findByBailIdOrderByDateEffetAsc(Integer bailId);
	
	Optional<RevisionLoyer> findTopByBailIdOrderByDateEffetDesc(Integer bailId);
	
}

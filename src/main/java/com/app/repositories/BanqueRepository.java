package com.app.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.app.entities.administration.Banque;

@Repository
public interface BanqueRepository extends JpaRepository<Banque, Long>{

	@Query("SELECT b FROM Banque b " +
		       "WHERE LOWER(b.bansigle) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
		       "   OR LOWER(b.banlibelle) LIKE LOWER(CONCAT('%', :keyword, '%'))")
		List<Banque> search(@Param("keyword") String keyword);
	
	//
	Page<Banque> findByBansigleContainingIgnoreCaseOrBanlibelleContainingIgnoreCase(String sigle, String libelle, Pageable pageable);


}

package com.app.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.app.entities.administration.Site;

@Repository
public interface SiteRepository extends JpaRepository<Site, String>{

	@Query("SELECT sit FROM Site sit WHERE (sit.sitCode =: sitCode or sit.sitSiteCode =: sitCode)")	
	Optional<Site> findByIdSite(@Param("sitCode") String sitCode);
	
	/*
	 * @Query("SELECT sitCode, CASE WHEN si.sitSiteCode is null then UPPER(sitLibelle) else LOWER(sitLibelle) end libelle FROM Site si ORDER BY si.sitCode"
	 * ) List<Site> findAllSiteOrder();
	 */
	
}

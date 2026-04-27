package com.app.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.app.entities.administration.TStatuts;

@Repository
public interface TStatutsRepository extends JpaRepository<TStatuts, String>{

	@Query("SELECT t FROM TStatuts t WHERE t.staCode = :staCode")
	Optional<TStatuts> findByStaaCode(@Param("staCode") String staCode);

}

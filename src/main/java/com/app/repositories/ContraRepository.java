package com.app.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.app.entities.recouvre.Bail;

//import com.app.entities.recouvre.Contra;

@Repository
public interface ContraRepository extends JpaRepository<Bail, Integer>{
	
	@Query(value = """
		    SELECT 
		        t.connumero AS connumero,
		        l.locnom AS locnom,
		        l.loccel AS loccel,
		        l.loctel AS loctel,
		        l.locemail AS locemail,
		        i.immlibelle AS immlibelle,
		        u.usglibelle AS usglibelle,
		        p.pronom AS pronom,
		        t.conloyer as conloyer
		    FROM t_contra t
		    JOIN t_locat l ON t.conlocat = l.loccode
		    JOIN t_immeub m ON t.conimmeub = m.imecode
		    JOIN t_immob i ON m.imecode = i.immcodeimm
		    JOIN t_usage u ON t.conusage = u.usgnumero
		    JOIN t_prop p ON t.conprop = p.procode
		    """, nativeQuery = true)
		List<ContratSelectProjection> findContratDetailsNative();

	//
	    @Query(value = """
	        SELECT t.connumero, 
	               l.locnom, 
	               l.loccel, 
	               l.loctel, 
	               l.locemail, 
	               i.immlibelle, 
	               u.usglibelle, 
	               p.pronom
	        FROM t_contra t
	        JOIN t_locat l ON t.conlocat = l.loccode
	        JOIN t_immeub m ON t.conimmeub = m.imecode
	        JOIN t_immob i ON m.imecode = i.immcodeimm
	        JOIN t_usage u ON t.conusage = u.usgnumero
	        JOIN t_prop p ON t.conprop = p.procode
	        WHERE t.connumero = :connumero
	        """, nativeQuery = true)
	    Object findContratDetailsByNumero(@Param("connumero") String connumero);
	    
}

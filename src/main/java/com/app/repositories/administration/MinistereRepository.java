package com.app.repositories.administration;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.app.entities.administration.Ministere;
import com.app.repositories.base.BaseRepository;

public interface MinistereRepository extends BaseRepository<Ministere, Integer>
{

    public Optional<Ministere> findByCode(String code);

	/*
	 * @Query("select distinct m from GouvMinistere gm join gm.gouvernement g join gm.ministere m  where g.actif = true order by m.ordre"
	 * ) public List<Ministere> findAllActive();
	 */

    @Query("select s from Ministere s where lower(s.code) like concat('%',concat(lower(:term), '%')) or lower(s.libelle) like concat('%',concat(lower(:term), '%')) "
            + " order by s.libelle asc")
    public List<Ministere> findByTerm(@Param("term") String term);

}

package com.app.repositories.administration;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import com.app.entities.administration.Typerole;
import com.app.repositories.base.BaseRepository;

public interface TyperoleRepository extends BaseRepository<Typerole, Integer> {

    /* =========================
       Méthodes dérivées
       ========================= */

    List<Typerole> findAllByOrderByLibelleAsc();

    /* =========================
       Requêtes JPQL explicites
       ========================= */

    @Query("""
        select t
        from Typerole t
        where t.code <> 'SSADM'
        order by t.libelle asc
    """)
    List<Typerole> findAllTypeRole();
}

package com.app.repositories.administration;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.app.entities.administration.Role;
import com.app.repositories.base.BaseRepository;

public interface RoleRepository extends BaseRepository<Role, Integer> {

    /* =========================
       Méthodes simples
       ========================= */

    List<Role> findByIdNot(int id);

    Optional<Role> findByLibelle(String libelle);

    List<Role> findAllByOrderById();

    /* ⚠️ Fonctionne, mais JPQL explicite est plus robuste (voir plus bas) */
    List<Role> findAllByOrderByMinistere_CodeAscTypeRole_CodeAsc();

    Optional<Role> findByCode(String code);

    /* =========================
       Requêtes JPQL corrigées
       ========================= */

    @Query("""
        select r
        from Role r
        where r.typeRole.code = :typeRoleCode
          and r.ministere.id = :idMinistere
        order by r.code asc, r.libelle asc
    """)
    List<Role> findByTypeRoleCodeAndMinistereId(
        @Param("typeRoleCode") String typeRoleCode,
        @Param("idMinistere") int idMinistere
    );

    @Query("""
        select a.role
        from Assignation a
        where a.utilisateur.username = :username
          and a.role.typeRole.code = :typeRoleCode
          and a.role.typeRole.code <> 'SSADM'
    """)
    List<Role> findByUsernameAndTypeRole(
        @Param("username") String username,
        @Param("typeRoleCode") String typeRoleCode
    );

    @Query("""
        select r
        from Role r
        where r.typeRole.code <> 'SSADM'
          and (
                lower(r.code) like concat('%', lower(:term), '%')
             or lower(r.libelle) like concat('%', lower(:term), '%')
          )
    """)
    List<Role> findByTerm(@Param("term") String term);

    @Query("""
        select coalesce(
            max( to_number( substring(r.code, :depart), '99999' ) ),
            0
        ) + 1
        from Role r
        where r.code like concat(:partCode, '%')
    """)
    int findNextSequenceCode(
        @Param("partCode") String partCode,
        @Param("depart") int depart
    );

    @Query("""
        select r
        from Role r
        where r not in (
            select a.role
            from Assignation a
            where a.role.typeRole.code not in ('OBS', 'OBSS', 'SSADM', 'ADM')
        )
    """)
    List<Role> findAllNotUsed();

    @Query("""
        select r
        from Role r
        where r.typeRole.code <> 'SSADM'
          and r not in (
            select a.role
            from Assignation a
            where a.role.typeRole.code not in ('OBS', 'OBSS', 'ADM')
          )
    """)
    List<Role> findAllNotUsedWithAllRole();

    @Query("""
        select r
        from Role r
        where r.typeRole.code <> 'SSADM'
        order by r.ministere.code asc, r.typeRole.code asc
    """)
    List<Role> findAllRole();

    /* =========================
       Chargement avec assignations
       ========================= */

    @Query("""
        select distinct r
        from Role r
        left join fetch r.assignations
        where r.id = :id
    """)
    Optional<Role> findByIdWithAssignations(@Param("id") int id);
}

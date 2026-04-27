package com.app.entities.administration;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.envers.Audited;

import com.app.entities.BaseEntityWithCodeLibelle;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Audited
@Entity
@Table(name = "t_role", indexes = {
        @Index(name = "idx_role_type", columnList = "type_role_id"),
        @Index(name = "idx_role", columnList = "type_role_id, ministere_id"),
        @Index(name = "idx_ministere", columnList = "ministere_id") })
@DynamicUpdate
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Role extends BaseEntityWithCodeLibelle {

    private static final long serialVersionUID = 1L;
    
    @ManyToOne
    @JoinColumn(name = "type_role_id", nullable = false, referencedColumnName = "idt")
    private Typerole typeRole;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", referencedColumnName = "idt")
    private Role parent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ministere_id", referencedColumnName = "idt" , nullable = true)
    private Ministere ministere;

    @OneToMany(mappedBy = "role", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Assignation> assignations = new ArrayList<>();
    
    @Transient
    @JsonIgnore
    public String getUtilisateurRole() {
        if (assignations == null || assignations.isEmpty()) {
            return getLibelle();
        }

        return getLibelle() + " - " +
               assignations.stream()
                   .map(Assignation::getUtilisateur)
                   .filter(u -> u != null)
                   .sorted(
                       Comparator.comparing(
                           Utilisateur::getFullName,
                           String.CASE_INSENSITIVE_ORDER
                       )
                   )
                   .map(Utilisateur::getFullName)
                   .collect(Collectors.joining(", "));
    }

    
   // public Role() {}

    public Role(Typerole typeRole, Ministere ministere, String code, String libelle) {
        this.typeRole = typeRole;
        this.ministere = ministere;
        setCode(code);
        setLibelle(libelle);
    }

    @Override
    public String toString() {
        return getCode() + " - " + getLibelle();
    }
}


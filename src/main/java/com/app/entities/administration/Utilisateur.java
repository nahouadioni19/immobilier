package com.app.entities.administration;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import jakarta.persistence.*;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.envers.Audited;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;

import com.app.entities.BaseEntity;
import com.app.utils.Constants;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Audited
@Entity
@Table(name = "t_utilisateur")
@DynamicUpdate
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Utilisateur extends BaseEntity implements UserDetails {

    private static final long serialVersionUID = 1L;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agence_id", nullable = false)
    private Agence agence;
    
    @Column(nullable = false)
    private String matricule;

    @Column(length = 15)
    private String titre;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String prenoms;

    @Column(length = 15)
    private String telephone;

    private String email;

    @Column(name = "nom_utilisateur", unique = true, nullable = false)
    private String username;

    @Column(name = "mot_de_passe", nullable = false)
    private String password;

    @Transient
    private String passwordClear;

    @Column(name = "ip_connexion")
    private String ipConnexion;

    @Column(name = "date_derniere_connexion")
    private LocalDateTime lastConnexionDate;

    @ManyToOne
    @JoinColumn(name = "ministere_id", referencedColumnName = "idt")
    private Ministere ministere;

    @OneToMany(mappedBy = "utilisateur", 
    		cascade = CascadeType.ALL, 
    		orphanRemoval = true, 
    		fetch = FetchType.LAZY)
    private List<Assignation> assignations = new ArrayList<>();

    @Column(columnDefinition = "boolean default false")
    private boolean resetPwd;

    @Transient
    private transient String savePwd;
    
    @Column(nullable = false)
    private boolean enabled = true;

    // -----------------------
    // Constructeurs utiles
    // -----------------------
    public Utilisateur(String username, String password, String nom, String prenoms,
                       List<Assignation> assignations, String savePwd) {
        this.nom = nom;
        this.prenoms = prenoms;
        this.username = username;
        this.password = password;
        this.assignations = assignations != null ? assignations : new ArrayList<>();
        this.savePwd = savePwd;
    }

    // -----------------------
    // Méthodes utilitaires
    // -----------------------
    @PrePersist
    public void prePersist() {
        if (this.assignations != null) {
            this.assignations.forEach(assignation -> {
                assignation.setUtilisateur(this);
                assignation.setModifiePar(getModifiePar());
            });
        }
    }

    @Transient
    public String getFullName() {
        return this.nom.trim() + (StringUtils.hasText(this.prenoms) ? " " + this.prenoms.trim() : "");
    }

    @Transient
    public String getInitials() {
        String nomComplet = this.nom.trim() +
                (StringUtils.hasText(this.prenoms) ? " " + this.prenoms.trim() : "");

        return nomComplet.trim().isEmpty() ? "" :
                Arrays.stream(nomComplet.split(" "))
                        .filter(StringUtils::hasText)
                        .map(s -> String.valueOf(s.toUpperCase().charAt(0)))
                        .collect(Collectors.joining());
    }

    public boolean isDefaultUser() {
        return Constants.DEFAULT_USER_NAME.equals(this.username);
    }

    public void addAssignation(Assignation assignation) {
        assignations.add(assignation);
        assignation.setUtilisateur(this);
    }

    public void removeAssignation(Assignation assignation) {
        assignations.remove(assignation);
        assignation.setUtilisateur(null);
    }

    // -----------------------
    // Implémentation UserDetails
    // -----------------------
    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (assignations == null) return Collections.emptyList();
        return assignations.stream()
                .filter(a -> a.getRole() != null && a.getRole().getLibelle() != null)
                .map(a -> new SimpleGrantedAuthority("ROLE_" + a.getRole().getLibelle().toUpperCase()))
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
        
    @Override
    public boolean isEnabled() {
        return enabled;
    }

}

package com.app.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import com.app.entities.administration.Assignation;
//import com.app.entities.administration.Menu;
import com.app.entities.administration.Utilisateur;
import com.app.entities.referentiel.Exercice;
import com.app.utils.Constants;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
public class UserPrincipal extends User {

    private static final long serialVersionUID = 1L;

    private Utilisateur utilisateur;

    private List<Assignation> assignations = new ArrayList<>();
    private Map<String, List<String>> menuKeyToActions = new HashMap<>();
    private Assignation assignationCourant;

    private boolean loggedIn;
    private Exercice exercice;
    private List<Exercice> exercices = new ArrayList<>();

    @Accessors(fluent = true)
    private boolean hasMultipleRole;

    @Accessors(fluent = true)
    private boolean hasRole;

    /*public UserPrincipal(Utilisateur utilisateur, Collection<? extends GrantedAuthority> authorities) {
        super(utilisateur.getUsername(), utilisateur.getPassword(), authorities);
        this.utilisateur = utilisateur;
        this.loggedIn = true;
    }*/
    
    public UserPrincipal(Utilisateur utilisateur,
            Collection<? extends GrantedAuthority> authorities) {

		super(
		utilisateur.getUsername(),
		utilisateur.getPassword(),
		utilisateur.isEnabled(),   // 🔥 ICI on lie le champ enabled
		true,                      // accountNonExpired
		true,                      // credentialsNonExpired
		true,                      // accountNonLocked
		authorities
		);
		
		this.utilisateur = utilisateur;
		this.loggedIn = true;
	}


    public boolean isActeur(String acteurLibelle) {
        String typeRoleCode = (this.assignationCourant != null
                && this.assignationCourant.getRole() != null
                && this.assignationCourant.getRole().getTypeRole() != null)
                        ? this.assignationCourant.getRole().getTypeRole().getCode()
                        : null;
        return typeRoleCode != null && typeRoleCode.equals(acteurLibelle);
    }

    public boolean isDefaultUser() {
        var noRole = this.assignationCourant == null
                || this.assignationCourant.getRole() == null
                || this.assignationCourant.getRole().getTypeRole() == null
                || this.assignationCourant.getRole().getTypeRole().getCode() == null;
        return noRole && (getUtilisateur().isDefaultUser() || isActeur(Constants.ROLE_DEVELOPEUR));
    }

    public boolean isAdmin() {
        return isActeur(Constants.ROLE_ADMIN);
    }

    public boolean isActeurMinistere() {
        return isActeur(Constants.ROLE_PF);
    }

    public boolean isActeurPrimature() {
        return isActeur(Constants.ROLE_PRIM);
    }

    public boolean isActeurObservateur() {
        return isActeur(Constants.TYPE_ROLE_OBSERVATEUR);
    }

    public boolean isAgentRecouv() {
        return isActeur(Constants.ROLE_RECOUV);
    }
    
    public boolean isAgentDirec() {
        return isActeur(Constants.ROLE_DIREC);
    }
    
    public boolean isAgentSecret() {
        return isActeur(Constants.ROLE_SECRET);
    }
    
    public boolean isViewObservateur() {
        return isDefaultUser() || isActeurObservateur();
    }
    
    public String getFullName() {
        if (utilisateur != null) {
            return utilisateur.getPrenoms() + " " + utilisateur.getNom();
        }
        return "Utilisateur";
    }
    
    @Override
    public boolean isEnabled() {
        return utilisateur.isEnabled();
    }

    public Utilisateur getUtilisateur() {
        return utilisateur;
    }
    
    public Integer getId() {
        if (utilisateur != null) {
            return utilisateur.getId();
        }
        return null;
    }
    
}
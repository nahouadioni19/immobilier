package com.app.dto;

import java.util.ArrayList;
import java.util.List;

import com.app.entities.administration.Assignation;
import com.app.entities.administration.Ministere;
import com.app.entities.administration.Utilisateur;

public class UtilisateurForm {
	private Utilisateur user;
    private List<Assignation> assignations = new ArrayList<>();
    
    public Utilisateur getUtilisateur() {
		
		if (user == null) {
			user = new Utilisateur();
		}

		// Initialisation obligatoire pour éviter les NullPointerException en Thymeleaf
		if (user.getMinistere() == null) {
			user.setMinistere(new Ministere());
		}
		
		return user;
	}

	public List<Assignation> getAssignations() {
		return assignations;
	}

	public void setAssignations(List<Assignation> assignations) {
		this.assignations = assignations;
	}

	public void setUtilisateur(Utilisateur user) {
		this.user = user;
	}
	
}

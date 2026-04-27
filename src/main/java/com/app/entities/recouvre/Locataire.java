package com.app.entities.recouvre;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.envers.Audited;

import com.app.entities.BaseEntity;
import com.app.entities.administration.Agence;
import com.app.entities.administration.Site;
import com.app.entities.administration.Utilisateur;
import com.app.entities.referentiel.Pays;
import com.app.entities.referentiel.Profession;
import com.app.enums.Sexe;
import com.app.enums.StatutAppartement;
import com.app.enums.TypeLocataire;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Audited
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "t_locataire")
@DynamicUpdate
//
public class Locataire extends BaseEntity{
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agence_id", nullable = false)
    private Agence agence;
	
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = true, length = 30)
    private TypeLocataire type;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = true, length = 20)
    private Sexe sexe;
    
    @ManyToOne	  
	@JoinColumn(name = "profession_id", nullable = true, referencedColumnName ="idt") 
	private Profession profession;
    
    @ManyToOne	  
	@JoinColumn(name = "pays_id", nullable = true, referencedColumnName ="idt") 
	private Pays pays;
    
    private int perschg;
    
    @Column(length = 20)
    private String typepiece;
    
    @Column(length = 50)
    private String nomContact;
    
    @Column(length = 50)
    private String prenomContact;
    
    @Column(length = 50)
    private String emailContact;
    
    @Column(length = 50)
    private String telContact;
    
    @Column(name = "document_path")
	private String documentPath;
}

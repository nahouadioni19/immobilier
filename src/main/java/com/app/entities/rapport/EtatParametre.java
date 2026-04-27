package com.app.entities.rapport;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.envers.Audited;

import com.app.entities.BaseEntity;
import com.app.entities.administration.Parametre;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Audited
@Entity
@Table(name = "t_parametre_etat")
@Getter
@Setter
@NoArgsConstructor
@DynamicUpdate
//@SequenceGenerator(name = "parametre_gen", sequenceName = "etat_parametre_seq", allocationSize = 1)
public class EtatParametre extends BaseEntity {

    private static final long serialVersionUID = 1L;
    
	/*
	 * @Override
	 * 
	 * @GeneratedValue(strategy = GenerationType.SEQUENCE, generator =
	 * "parametre_gen")
	 * 
	 * @Id public Integer getId() { return super.getId(); }
	 */
    
    @ManyToOne
    @JoinColumn(name = "ref_etat_id", referencedColumnName = "idt", nullable = false)
    private RefEtat refEtat;
    @ManyToOne
    @JoinColumn(name = "parametre_id", referencedColumnName = "idt", nullable = false)
    private Parametre parametre;

    @Column(name = "autre_libelle")
    private String autreLibelle;

    @Column(name = "code_param")
    private String codeParam;

    @Column(columnDefinition = "boolean default false")
    private boolean obligatoire = false;

    @Column(columnDefinition = "boolean default false")
    private boolean numericValue = false;

    @Column(nullable = true)
    private String valeur;

    private int ordre;

    @Column(columnDefinition = "boolean default false")
    private boolean hide = false;
}

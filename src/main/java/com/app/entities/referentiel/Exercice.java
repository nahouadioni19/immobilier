package com.app.entities.referentiel;

import java.time.LocalDate;

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
import org.springframework.format.annotation.DateTimeFormat;

import com.app.entities.BaseEntity;
import com.app.utils.Constants;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Audited
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "t_exercice")
@DynamicUpdate
//@SequenceGenerator(name = "exercice_gen", sequenceName = "exercice_seq", allocationSize = 1)
public class Exercice extends BaseEntity {
	/*
	 * @Override
	 * 
	 * @GeneratedValue(strategy = GenerationType.SEQUENCE, generator =
	 * "exercice_gen")
	 * 
	 * @Id public Integer getId() { return super.getId(); }
	 */
	
    @Column(nullable = false, unique = true)
    private int annee;

    @DateTimeFormat(pattern = Constants.FORMAT_DATE_DEFAULT)
    @Column(name = "date_debut", nullable = false)
    private LocalDate dateDebut;
    
    @DateTimeFormat(pattern = Constants.FORMAT_DATE_DEFAULT)
    @Column(name = "date_fin", nullable = false)
    private LocalDate dateFin;

    @ManyToOne
    @JoinColumn(name="statut_id", referencedColumnName = "idt", nullable = true)
    private Statut statut;

}

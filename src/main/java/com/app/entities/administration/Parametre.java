package com.app.entities.administration;

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

import com.app.entities.BaseEntityWithCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Audited
@Entity
@Table(name = "t_parametre")
@DynamicUpdate
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
//@SequenceGenerator(name = "parametre_gen", sequenceName = "parametre_seq", allocationSize = 1)
public class Parametre extends BaseEntityWithCode {

    private static final long serialVersionUID = 1L;

	/*
	 * @Override
	 * 
	 * @GeneratedValue(strategy = GenerationType.SEQUENCE, generator =
	 * "parametre_gen")
	 * 
	 * @Id public Integer getId() { return super.getId(); }
	 */
    
    private String valeur;

    @ManyToOne
    @JoinColumn(name = "type_parametre_id", nullable = false, referencedColumnName = "idt")
    private TypeParametre type;
}

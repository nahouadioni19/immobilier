package com.app.entities.administration;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.envers.Audited;

import com.app.entities.BaseEntityWithCodeLibelle;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Audited
@Entity
@Table(name = "t_type_parametre")
@DynamicUpdate
@Getter
@Setter
@NoArgsConstructor
//@SequenceGenerator(name = "type_parametre_gen", sequenceName = "type_parametre_seq", allocationSize = 1)
public class TypeParametre extends BaseEntityWithCodeLibelle {

    private static final long serialVersionUID = 1L;
    
	/*
	 * @Override
	 * 
	 * @GeneratedValue(strategy = GenerationType.SEQUENCE, generator =
	 * "type_parametre_gen")
	 * 
	 * @Id public Integer getId() { return super.getId(); }
	 */
}

package com.app.entities.administration;

import jakarta.persistence.Column;
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
@Getter
@Setter
@NoArgsConstructor
@Table(name = "t_ministere")
@DynamicUpdate
//@SequenceGenerator(name = "ministere_gen", sequenceName = "ministere_seq", allocationSize = 1)
public class Ministere extends BaseEntityWithCodeLibelle {

	/*
	 * @Override
	 * 
	 * @GeneratedValue(strategy = GenerationType.SEQUENCE, generator =
	 * "ministere_gen")
	 * 
	 * @Id public Integer getId() { return super.getId(); }
	 */
	
    @Column(columnDefinition = "int4 default 0")
    private int ordre;
}

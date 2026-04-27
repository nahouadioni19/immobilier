package com.app.entities.rapport;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.envers.Audited;

import com.app.entities.BaseEntityWithLibelle;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Audited
@Entity
@Table(name = "t_groupe_etat")
@Getter
@Setter
@NoArgsConstructor
@DynamicUpdate
//@SequenceGenerator(name = "groupe_gen", sequenceName = "groupe_etat_seq", allocationSize = 1)
public class GroupeEtat extends BaseEntityWithLibelle {

    private static final long serialVersionUID = 1L;
    
	/*
	 * @Override
	 * 
	 * @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "groupe_gen")
	 * 
	 * @Id public Integer getId() { return super.getId(); }
	 */
    
    private int ordre;

}
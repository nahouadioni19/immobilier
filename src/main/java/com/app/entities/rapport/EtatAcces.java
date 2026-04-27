package com.app.entities.rapport;

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
import com.app.entities.administration.Typerole;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Audited
@Entity
@Table(name = "t_acces_etat")
@Getter
@Setter
@NoArgsConstructor
@DynamicUpdate
//@SequenceGenerator(name = "etat_gen", sequenceName = "etat_acces_seq", allocationSize = 1)
public class EtatAcces extends BaseEntity {

    private static final long serialVersionUID = 1L;
    
	/*
	 * @Override
	 * 
	 * @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "etat_gen")
	 * 
	 * @Id public Integer getId() { return super.getId(); }
	 */

    @ManyToOne
    @JoinColumn(name = "ref_etat_id", referencedColumnName = "idt", nullable = false)
    private RefEtat refEtat;
    @ManyToOne
    @JoinColumn(name = "type_role_id", referencedColumnName = "idt", nullable = false)
    private Typerole typeRole;

}

package com.app.entities.administration;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.envers.Audited;

import com.app.entities.BaseEntity;
import com.app.enums.ActionMenu;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Audited
@Entity
@Table(name = "t_droit_acces", indexes = {
        @Index(name = "idx_type_role", columnList = "type_role_id")})
     //   @Index(name = "idx_menu", columnList = "menu_id") })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
//@SequenceGenerator(name = "droit_gen", sequenceName = "droit_acces_seq", allocationSize = 1)
@DynamicUpdate
public class DroitAcces extends BaseEntity {

    private static final long serialVersionUID = 1L;

	/*
	 * @Override
	 * 
	 * @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "droit_gen")
	 * 
	 * @Id public Integer getId() { return super.getId(); }
	 */
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "type_role_id", nullable = false, referencedColumnName = "idt")
    private Typerole typeRole;

    /*@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id", nullable = false, referencedColumnName = "idt")
    private Menu menu;*/

    @Enumerated(EnumType.STRING)
    private ActionMenu action;

    @Transient
    private String statut;
}


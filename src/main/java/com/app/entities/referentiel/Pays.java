package com.app.entities.referentiel;

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
@Table(name = "t_pays")
@Getter
@Setter
@NoArgsConstructor
@DynamicUpdate
//@SequenceGenerator(name = "pays_gen", sequenceName = "pays_seq", allocationSize = 1)
public class Pays extends BaseEntityWithCodeLibelle {

    private static final long serialVersionUID = 1L;
    
	

}

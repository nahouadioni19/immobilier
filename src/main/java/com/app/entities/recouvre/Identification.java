package com.app.entities.recouvre;

import java.time.LocalDate;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.envers.Audited;

import com.app.entities.BaseEntity;
import com.app.entities.administration.Agence;
import com.app.entities.administration.Utilisateur;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "t_identification")
@DynamicUpdate
//@SequenceGenerator(name = "identification_gen", sequenceName = "encaisse_seq", allocationSize = 1)
public class Identification extends BaseEntity {    
		
    @Column(name = "ide_numero", nullable = false)
    private long ideNumero;

    @Column(name = "ide_etat")
    private Boolean ideEtat = false; // Boolean pour nullable + default false

    @ManyToOne
    @JoinColumn(name = "utilisateur_id", referencedColumnName = "idt")
    private Utilisateur utilisateur;

    @ManyToOne
    @JoinColumn(name = "carnet_id", referencedColumnName = "idt")
    private Carnet carnet;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agence_id", nullable = false)
    private Agence agence;

}

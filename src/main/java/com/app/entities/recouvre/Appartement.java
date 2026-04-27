package com.app.entities.recouvre;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.envers.Audited;

import com.app.entities.BaseEntity;
import com.app.entities.administration.Agence;
import com.app.entities.administration.Site;
import com.app.enums.StatutAppartement;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Audited
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "t_appartement")
@DynamicUpdate
//@SequenceGenerator(name = "appartement_gen", sequenceName = "appartement_seq", allocationSize = 1)
public class Appartement extends BaseEntity {
	
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "immeub_id")
	@JsonBackReference
    private Immeuble immeuble;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agence_id", nullable = false)
    private Agence agence;

    @NotBlank
    @Column(nullable = false, length = 20)
    private String numAppart;

    @NotBlank
    @Column(nullable = false, length = 100)
    private String libelle;

    @PositiveOrZero
    @Column(nullable = false)
    private Integer etage = 0;

    @PositiveOrZero
    @Column(nullable = false)
    private Integer nombreChambres = 0;

    @PositiveOrZero
    @Column(nullable = false)
    private Integer nombreSallesBain = 0;

    @PositiveOrZero
    @Column(nullable = false)
    private Integer nombreSalons = 0;

    @PositiveOrZero
    @Column(nullable = false)
    private Integer nombreBalcons = 0;

    @PositiveOrZero
    @Column(nullable = false)
    private Long loyerMensuel = 0L;

    @PositiveOrZero
    @Column(nullable = false)
    private Long chargesMensuelles = 0L;

    @PositiveOrZero
    @Column(nullable = false)
    private Long caution = 0L;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatutAppartement statut = StatutAppartement.LIBRE;

    @OneToMany(mappedBy = "appartement",
               fetch = FetchType.LAZY,
               cascade = CascadeType.ALL,
               orphanRemoval = true)
    private List<Bail> baux = new ArrayList<>();
}
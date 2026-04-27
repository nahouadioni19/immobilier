package com.app.entities.abonne;

import java.time.LocalDate;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.envers.Audited;
import org.springframework.format.annotation.DateTimeFormat;

import com.app.entities.BaseEntity;
import com.app.entities.administration.Agence;
import com.app.enums.Statut;
import com.app.utils.Constants;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Audited
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "t_abonnement")
@DynamicUpdate
public class Abonnement extends BaseEntity{
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agence_id", nullable = false)
    private Agence agence;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id", nullable = false)
    private Transaction transaction;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "operateur_id", nullable = false)
    private Operateur operateur;
	
	@DateTimeFormat(pattern = Constants.FORMAT_DATE_DEFAULT)
    @Column(name = "date_debut")
	private LocalDate dateDebut;
	
	@DateTimeFormat(pattern = Constants.FORMAT_DATE_DEFAULT)
    @Column(name = "date_fin")
	private LocalDate dateFin;
	
	@DateTimeFormat(pattern = Constants.FORMAT_DATE_DEFAULT)
    @Column(name = "date_paiement")
	private LocalDate datePaiement;
	
	@Min(0)
	@Column(name="montant")
	private Long montant = 0L;
	
	@Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Statut statut;

}

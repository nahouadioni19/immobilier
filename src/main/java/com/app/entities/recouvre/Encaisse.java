package com.app.entities.recouvre;

import java.time.LocalDate;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.envers.Audited;
import org.springframework.format.annotation.DateTimeFormat;

import com.app.entities.BaseEntity;
import com.app.entities.administration.Agence;
import com.app.entities.administration.Site;
import com.app.entities.administration.Utilisateur;
import com.app.utils.Constants;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "t_encaisse")
@DynamicUpdate

public class Encaisse extends BaseEntity{
	
	@ManyToOne
	@JoinColumn(name = "utilisateur_id", nullable = true, referencedColumnName ="idt") 
	private Utilisateur utilisateur;
	
	@ManyToOne
	@JoinColumn(name = "bail_id", nullable = true, referencedColumnName ="idt") 
	private Bail bail;
	
	@ManyToOne
	@JoinColumn(name = "identification_id", nullable = true, referencedColumnName ="idt") 
	private Identification identification;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agence_id", nullable = false)
    private Agence agence;
	
	@DateTimeFormat(pattern = Constants.FORMAT_DATE_DEFAULT)
	@Column
	private LocalDate encDate;
	
	@Min(0)
	@Column
	private Long encMontant = 0L;
	
	@Min(0)
	@Column
	private Long encPerdeb = 0L;
	
	@Min(0)
	@Column
	private Long encAndeb = 0L;
	
	@Min(0)
	@Column
	private Long encPerfin = 0L;
	
	@Min(0)
	@Column
	private Long encAnfin = 0L;
	
	@Min(0)
	@Column
	private Long enctotal = 0L;
	
	@Min(0)
	@Column
	private Long encloyer = 0L;
	
	@Column
	private boolean encvalide;
	
	@Min(0)
	@Column
	private Long encmois = 0L;
	
	@Min(0)
	@Column
	private Long encannee = 0L;
	
	@Column(name="enc_statut_retour")
	private String encStatutRetour;
	
	@Column(name="enc_mode")
	private String encMode;
	
	@Min(0)
	@Column(name="enc_arriere")
	private Long encArriere = 0L;
	
	@Column(name="enc_deb")
	private String encDeb;
	
	@Column(name="enc_fin")
	private String encFin;
	
	@Min(0)
	@Column(name="enc_penalite")
	private Long encPenalite = 0L;
	
	@Min(0)
	@Column(name="enc_net")
	private Long encNet = 0L;
	
	@Column(name="enc_num_chq")
	private String encNumChq;
	
	@Min(0)
	@Column(name="enc_repport")
	private Long encRepport = 0L;
	
	@Min(0)
	@Column(name="enc_mont_reppo")
	private Long encMontReppo = 0L;	
	
	@Column(name = "statut")
	private Integer statut = 0; // 0 = en attente, 1 = transmis
	
	private Integer filtreAgentId;

}

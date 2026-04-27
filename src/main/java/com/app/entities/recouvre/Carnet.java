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
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "t_carnet")
@DynamicUpdate
//@SequenceGenerator(name = "carnet_gen", sequenceName = "carnet_seq", allocationSize = 1)
public class Carnet extends BaseEntity{	
		
	@ManyToOne
    @JoinColumn(nullable = false, referencedColumnName = "idt", name = "utilisateur_id")
    private Utilisateur utilisateur;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agence_id", nullable = false)
    private Agence agence;
	
	@DateTimeFormat(pattern = Constants.FORMAT_DATE_DEFAULT)
	@Column(name="car_date")
	private LocalDate carDate;
	
	@Column(name="car_num_deb")
	private Long carNumDeb;
	
	@Column(name="car_num_fin")
	private Long carNumFin;
	
	@DateTimeFormat(pattern = Constants.FORMAT_DATE_DEFAULT)
	@Column(name="car_date_aff")
	private LocalDate carDateAff;
	
	@Column(name="car_nombre")
	private long carNombre;
	
	@Column(name="car_active")
	private boolean carActive;	
	
}

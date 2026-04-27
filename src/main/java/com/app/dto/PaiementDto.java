package com.app.dto;

import java.time.LocalDate;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class PaiementDto {

	private Integer id;
	private Integer agenceId;
	
    private LocalDate datePaiement;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private Long montant;    
    
    private String status;
}

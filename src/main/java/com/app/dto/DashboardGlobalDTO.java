package com.app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardGlobalDTO {

    private Long totalAppartements;
    private Long appartementsOccupes;
    private Long appartementsDisponibles;

    private Long totalLocataires;

    private Long bauxActifs;
    private Long bauxResilies;

    private Long montantLoyersMensuels;
    private Long montantLoyersAnnee;

    private Double tauxOccupation;
    
    private Long montantEncaisseMois;

    private Long montantEncaisseAnnee;

    private Long montantImpayes;
}
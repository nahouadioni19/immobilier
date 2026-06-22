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

    private Long totalAppartements = 0L;
    private Long appartementsOccupes = 0L;
    private Long appartementsDisponibles = 0L;

    private Long totalLocataires = 0L;

    private Long bauxActifs = 0L;
    private Long bauxResilies = 0L;

    private Long montantLoyersMensuels = 0L;
    private Long montantLoyersAnnee = 0L;

    private Double tauxOccupation =  0.0;
    
    private Long montantEncaisseMois = 0L;

    private Long montantEncaisseAnnee = 0L;
    
    private Long nombreBauxEnRetard = 0L;

    private Long montantImpayes = 0L;
    
    public static DashboardGlobalDTO empty() {
        return DashboardGlobalDTO.builder()
                .totalAppartements(0L)
                .appartementsOccupes(0L)
                .appartementsDisponibles(0L)
                .totalLocataires(0L)
                .bauxActifs(0L)
                .bauxResilies(0L)
                .montantLoyersMensuels(0L)
                .montantLoyersAnnee(0L)
                .tauxOccupation(0.0)
                .montantEncaisseMois(0L)
                .montantEncaisseAnnee(0L)
                .nombreBauxEnRetard(0L)
                .montantImpayes(0L)
                .build();
    }
}
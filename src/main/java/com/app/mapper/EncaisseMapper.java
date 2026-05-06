package com.app.mapper;

import com.app.dto.EncaisseListDto;
import com.app.entities.recouvre.Encaisse;
import org.springframework.stereotype.Component;

@Component
public class EncaisseMapper {

    /*public EncaisseListDto toDto(Encaisse e) {

        EncaisseListDto dto = new EncaisseListDto();

        dto.setId(e.getId());
        dto.setEncDate(e.getEncDate());
        dto.setEncMontant(e.getEncMontant());
        dto.setEncMode(e.getEncMode());

        dto.setLocataireNom(e.getBail().getLocataire().getNom());
        dto.setLocatairePrenom(e.getBail().getLocataire().getPrenom());

        dto.setAppartementNumero(e.getBail().getAppartement().getNumAppart());

        dto.setUtilisateurId(e.getUtilisateur().getId());
        dto.setUtilisateurNom(e.getUtilisateur().getNom());
        dto.setUtilisateurPrenoms(e.getUtilisateur().getPrenoms());

        return dto;
    }*/
}
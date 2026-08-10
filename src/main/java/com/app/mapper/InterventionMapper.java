package com.app.mapper;

import org.springframework.stereotype.Component;

import com.app.dto.InterventionDTO;
import com.app.entities.maintenance.Intervention;
import com.app.form.InterventionForm;

@Component
public class InterventionMapper {

    /**
     * Entity -> DTO
     */
    public InterventionDTO toDTO(Intervention entity) {

        if (entity == null) {
            return null;
        }

        InterventionDTO dto = new InterventionDTO();

        dto.setId(entity.getId());

        dto.setObjet(entity.getObjet());
        dto.setDescription(entity.getDescription());

        dto.setDateDeclaration(entity.getDateDeclaration());
        dto.setDatePrevue(entity.getDatePrevue());
        dto.setDateDebut(entity.getDateDebut());
        dto.setDateFin(entity.getDateFin());

        dto.setStatut(entity.getStatut());

        dto.setCoutEstime(entity.getCoutEstime());
        dto.setCoutReel(entity.getCoutReel());

        dto.setObservation(entity.getObservation());

        dto.setUrgente(entity.getUrgente());

        if (entity.getAgence() != null) {
            dto.setAgenceId(entity.getAgence().getId());
            dto.setAgenceNom(entity.getAgence().getNom());
        }

        if (entity.getAppartement() != null) {
            dto.setAppartementId(entity.getAppartement().getId());
            dto.setAppartementLibelle(entity.getAppartement().getLibelle());
        }

        if (entity.getLocataire() != null) {
            dto.setLocataireId(entity.getLocataire().getId());
            dto.setLocataireNomComplet(entity.getLocataire().getNom());
        }

        if (entity.getPrestataire() != null) {
            dto.setPrestataireId(entity.getPrestataire().getId());
            dto.setPrestataireNom(entity.getPrestataire().getNom());
        }
        
        if (entity.getBailleur() != null) {
            dto.setBailleurId(entity.getBailleur().getId());
            dto.setBailleurNom(entity.getBailleur().getNom());
            dto.setBailleurPrenom(entity.getBailleur().getPrenom());
        }

        if (entity.getTypeIntervention() != null) {
            dto.setTypeInterventionId(entity.getTypeIntervention().getId());
            dto.setTypeInterventionLibelle(entity.getTypeIntervention().getLibelle());
        }

        return dto;
    }

    /**
     * Entity -> Form
     */
    public InterventionForm toForm(Intervention entity) {

        if (entity == null) {
            return null;
        }

        InterventionForm form = new InterventionForm();

        form.setId(entity.getId());

        form.setObjet(entity.getObjet());
        form.setDescription(entity.getDescription());

        form.setDateDeclaration(entity.getDateDeclaration());
        form.setDatePrevue(entity.getDatePrevue());
        form.setDateDebut(entity.getDateDebut());
        form.setDateFin(entity.getDateFin());

        form.setStatut(entity.getStatut());

        form.setCoutEstime(entity.getCoutEstime());
        form.setCoutReel(entity.getCoutReel());

        form.setObservation(entity.getObservation());

        form.setUrgente(entity.getUrgente());

        if (entity.getAppartement() != null) {
            form.setAppartementId(entity.getAppartement().getId());
        }

        if (entity.getLocataire() != null) {
            form.setLocataireId(entity.getLocataire().getId());
        }

        if (entity.getPrestataire() != null) {
            form.setPrestataireId(entity.getPrestataire().getId());
        }
        
        if (entity.getBailleur() != null) {
            form.setBailleurId(entity.getBailleur().getId());
        }

        if (entity.getTypeIntervention() != null) {
            form.setTypeInterventionId(entity.getTypeIntervention().getId());
        }

        return form;
    }

    /**
     * Form -> Entity
     *
     * Les relations (Appartement, Prestataire, Locataire, TypeIntervention,
     * Agence) sont volontairement renseignées dans le service.
     */
    public void updateEntity(InterventionForm form, Intervention entity) {

        entity.setObjet(form.getObjet());
        entity.setDescription(form.getDescription());

        entity.setDateDeclaration(form.getDateDeclaration());
        entity.setDatePrevue(form.getDatePrevue());
        entity.setDateDebut(form.getDateDebut());
        entity.setDateFin(form.getDateFin());

        entity.setStatut(form.getStatut());

        entity.setCoutEstime(form.getCoutEstime());
        entity.setCoutReel(form.getCoutReel());

        entity.setObservation(form.getObservation());

        entity.setUrgente(Boolean.TRUE.equals(form.getUrgente()));
    }
}
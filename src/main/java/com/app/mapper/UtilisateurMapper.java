package com.app.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.app.dto.AssignationDTO;
import com.app.dto.UtilisateurDTO;
import com.app.entities.administration.Assignation;
import com.app.entities.administration.Utilisateur;

public class UtilisateurMapper {

    public static UtilisateurDTO toDTO(Utilisateur user) {

        if (user == null) {
            return null;
        }

        UtilisateurDTO dto = new UtilisateurDTO();

        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setNom(user.getNom());
        dto.setPrenoms(user.getPrenoms());
        dto.setMatricule(user.getMatricule());
        dto.setTelephone(user.getTelephone());
        dto.setEmail(user.getEmail());
        dto.setTitre(user.getTitre());

        if (user.getAgence() != null) {
            dto.setAgenceId(user.getAgence().getId());
        }

        // Assignations -> DTO
        if (user.getAssignations() != null) {

            List<AssignationDTO> assignations = new ArrayList<>();

            int index = 0;

            for (Assignation a : user.getAssignations()) {

                AssignationDTO adto = new AssignationDTO();

                adto.setId(a.getId());

                if (a.getRole() != null) {
                    adto.setRoleId(a.getRole().getId());
                    adto.setRoleCode(a.getRole().getCode());
                    adto.setRoleLibelle(a.getRole().getLibelle());
                }

                adto.setDateDebut(a.getDateDebut());
                adto.setDateFin(a.getDateFin());
                adto.setCourant(a.isCourant());

                if (a.isCourant()) {
                    dto.setProfilDefautIndex(index);
                }

                assignations.add(adto);
                index++;
            }

            dto.setAssignations(assignations);
        }

        return dto;
    }

    public static void updateEntity(Utilisateur user, UtilisateurDTO dto) {

        if (user == null || dto == null) {
            return;
        }

        user.setUsername(dto.getUsername());
        user.setNom(dto.getNom());
        user.setPrenoms(dto.getPrenoms());
        user.setMatricule(dto.getMatricule());
        user.setTelephone(dto.getTelephone());
        user.setEmail(dto.getEmail());
        user.setTitre(dto.getTitre());

        // Les assignations sont gérées dans le contrôleur/service
    }
}

/*public class UtilisateurMapper {

    public static UtilisateurDTO toDTO(Utilisateur user) {
        if (user == null) return null;

        UtilisateurDTO dto = new UtilisateurDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setNom(user.getNom());
        dto.setPrenoms(user.getPrenoms());
        dto.setMatricule(user.getMatricule());
        dto.setTelephone(user.getTelephone());
        dto.setEmail(user.getEmail());
        dto.setTitre(user.getTitre());

        // ⚡ Transforme les assignations en DTO
        if (user.getAssignations() != null) {
            dto.setAssignations(
                user.getAssignations().stream().map(a -> {
                    AssignationDTO adto = new AssignationDTO();
                    adto.setId(a.getId());
                    adto.setRoleId(a.getRole() != null ? a.getRole().getId() : null);
                    adto.setRoleCode(a.getRole() != null ? a.getRole().getCode() : null);
                    adto.setRoleLibelle(a.getRole() != null ? a.getRole().getLibelle() : null);
                    adto.setDateDebut(a.getDateDebut());
                    adto.setDateFin(a.getDateFin());
                    adto.setCourant(a.isCourant());
                    return adto;
                }).collect(Collectors.toList())
            );
        }
        
        if(user.getAgence() != null) {
        	dto.setAgenceId(user.getAgence().getId());
        }

        return dto;
    }

    public static void updateEntity(Utilisateur user, UtilisateurDTO dto) {
        user.setUsername(dto.getUsername());
        user.setNom(dto.getNom());
        user.setPrenoms(dto.getPrenoms());
        user.setMatricule(dto.getMatricule());
        user.setTelephone(dto.getTelephone());
        user.setEmail(dto.getEmail());
        user.setTitre(dto.getTitre());
        // Assignations sont gérées dans le controller
    }
}*/

package com.app.mapper;

import java.util.stream.Collectors;

import com.app.dto.AssignationDTO;
import com.app.dto.UtilisateurDTO;
import com.app.entities.administration.Assignation;
import com.app.entities.administration.Utilisateur;

public class UtilisateurMapper {

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
                    adto.setRoleLibelle(a.getRole() != null ? a.getRole().getLibelle() : null);
                    adto.setDateDebut(a.getDateDebut());
                    adto.setDateFin(a.getDateFin());
                    return adto;
                }).collect(Collectors.toList())
            );
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
}




/*
 * package com.app.mapper;
 * 
 * import com.app.dto.AssignationDTO; import com.app.dto.UtilisateurDTO; import
 * com.app.entities.administration.Utilisateur;
 * 
 * public class UtilisateurMapper {
 * 
 * public static UtilisateurDTO toDTO(Utilisateur user) { UtilisateurDTO dto =
 * new UtilisateurDTO(); dto.setId(user.getId());
 * dto.setUsername(user.getUsername()); dto.setNom(user.getNom());
 * dto.setPrenoms(user.getPrenoms()); dto.setMatricule(user.getMatricule());
 * dto.setTelephone(user.getTelephone()); dto.setEmail(user.getEmail());
 * dto.setTitre(user.getTitre());
 * 
 * if (user.getAssignations() != null) { user.getAssignations().forEach(assign
 * -> { AssignationDTO adto = new AssignationDTO(); adto.setId(assign.getId());
 * adto.setRoleId(assign.getRole().getId());
 * adto.setRoleLibelle(assign.getRole().getLibelle());
 * adto.setDateDebut(assign.getDateDebut());
 * adto.setDateFin(assign.getDateFin()); dto.getAssignations().add(adto); }); }
 * return dto; } }
 */
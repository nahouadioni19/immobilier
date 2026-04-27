package com.app.service;

import com.app.dto.UserForm;
import com.app.entities.administration.Utilisateur;
import com.app.repositories.UserutilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserGrandPublicService {

    @Autowired private UserutilisateurRepository userRepository;
  //  @Autowired private BCryptPasswordEncoder passwordEncoder;
    @Autowired private EmailService emailService;

    public void save(UserForm userForm) {
        Utilisateur user = new Utilisateur();
        user.setNom(userForm.getNom());
        user.setPrenoms(userForm.getPrenoms());
        user.setEmail(userForm.getEmail());

        // 🔐 Générer un nom d’utilisateur unique
        String generatedUsername = genererNomUtilisateur(userForm.getPrenoms(), userForm.getNom());
        user.setUsername(generatedUsername);

        // 🔐 Encoder le mot de passe
//        user.setPassword(passwordEncoder.encode(userForm.getPassword()));
        user.setTelephone(userForm.getTelephone());

        // 💾 Enregistrement
        userRepository.save(user);

        // 📧 Envoi de mail de confirmation
        emailService.envoyerMailDeConfirmation(user.getEmail(), generatedUsername);
    }

    public String genererNomUtilisateur(String prenoms, String nom) {
        String base = (prenoms.charAt(0) + nom).toLowerCase().replaceAll("\\s+", "");
        String candidat = base;
        int compteur = 1;

        while (userRepository.existsByUsername(candidat)) {
            candidat = base + compteur;
            compteur++;
        }
        return candidat;
    }
}
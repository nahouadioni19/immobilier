package com.app.service.administration;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.entities.administration.Utilisateur;
import com.app.repositories.administration.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Mot de passe par défaut en clair (sera comparé via matches)
    private static final String DEFAULT_PASSWORD = "0000";

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Trouve un utilisateur par son nom d'utilisateur
     */
    public Utilisateur findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * Vérifie si le mot de passe d'un utilisateur est le mot de passe par défaut
     */
    public boolean isDefaultPassword(Utilisateur user) {
        if (user == null || user.getPassword() == null) {
            return false;
        }
        return passwordEncoder.matches(DEFAULT_PASSWORD, user.getPassword());
    }

    /**
     * Met à jour le mot de passe d'un utilisateur si le mot de passe actuel est correct
     */
    @Transactional
    public boolean updatePassword(String username, String currentPassword, String newPassword) {
        Utilisateur user = userRepository.findByUsername(username);
        if (user == null) {
            return false; // utilisateur introuvable
        }

        // Vérifier le mot de passe actuel
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            return false; // mot de passe actuel incorrect
        }

        // Encoder et mettre à jour le mot de passe
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return true;
    }

    /**
     * Crée un nouvel utilisateur avec mot de passe par défaut encodé
     */
    @Transactional
    public Utilisateur createUser(Utilisateur user) {
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(DEFAULT_PASSWORD));
        } else {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        return userRepository.save(user);
    }
    
    public Utilisateur findByUsernameWithRoles(String username) {

        return userRepository
                .findUserWithRoles(username)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

    }
}
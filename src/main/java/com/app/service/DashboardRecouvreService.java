package com.app.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.dto.DashboardDTO;
import com.app.entities.recouvre.Encaisse;
import com.app.repositories.recouvre.EncaisseRepository;

@Service
public class DashboardRecouvreService {

    @Autowired
    private EncaisseRepository encaisseRepository;

    public DashboardDTO getDashboard() {

        List<Encaisse> encaisses = encaisseRepository.findAll();

        int totalAPayer = 0;
        int totalPaye = 0;
        int totalRetard = 0;

        LocalDate today = LocalDate.now();

        for (Encaisse p : encaisses) {

            totalAPayer += p.getEncMontant();

            if (p.getEncDate() != null) {
                totalPaye += p.getEncMontant();
            } else {
                if (today.getDayOfMonth() > 10) {
                    totalRetard += p.getEncMontant();
                }
            }
        }

        return new DashboardDTO(totalAPayer, totalPaye, totalRetard);
    }
}
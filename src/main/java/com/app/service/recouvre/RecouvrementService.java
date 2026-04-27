package com.app.service.recouvre;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.app.dto.RecouvrementDTO;
import com.app.entities.recouvre.Bail;
import com.app.entities.recouvre.Loyann;
import com.app.enums.StatutBail;
import com.app.repositories.recouvre.BailRepository;
import com.app.repositories.recouvre.LoyannRepository;

@Service
public class RecouvrementService {

    @Autowired
    private BailRepository bailRepository;

    @Autowired
    private LoyannRepository loyannRepository;

    /**
     * Récupère la liste des recouvrements pour un mois et une année donnés.
     * Affiche aussi les locataires n'ayant jamais payé.
     */
    public Page<RecouvrementDTO> getRecouvrementsPage(int mois, int annee, Pageable pageable) {
        List<Bail> baux = bailRepository.findByStatut(StatutBail.ACTIF);

        // Conversion en DTO
        List<RecouvrementDTO> allRecouvrements = baux.stream().map(bail -> {
            Loyann l = loyannRepository.findByBailAndMoisAndAnnee(bail, mois, annee).orElse(null);

            RecouvrementDTO dto = new RecouvrementDTO();
            dto.setLocataireNom(bail.getLocataire().getNom());
            dto.setLoyerMensuel(bail.getMontantLoyer());

            if (l == null) {
                dto.setMontantPaye(0L);
                dto.setReste(bail.getMontantLoyer());
                dto.setStatut("IMPAYE");
            } else {
                dto.setMontantPaye(l.getMontantPaye());
                dto.setReste(l.getMontantDu() - l.getMontantPaye());

                if (l.getMontantPaye() >= l.getMontantDu()) {
                    dto.setStatut("PAYE");
                } else if (LocalDate.now().getDayOfMonth() > 10 && dto.getReste() > 0) {
                    dto.setStatut("RETARD");
                } else {
                    dto.setStatut("PARTIEL");
                }
            }
            return dto;
        }).toList();

        // Pagination manuelle sur la liste
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), allRecouvrements.size());
        List<RecouvrementDTO> pageContent = allRecouvrements.subList(start, end);

        return new PageImpl<>(pageContent, pageable, allRecouvrements.size());
    }
}
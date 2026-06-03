package com.app.service.recouvre;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.text.Normalizer;
import java.util.HashMap;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.app.dto.DashboardLoyerDTO;
import com.app.dto.DashboardLoyerMontantDTO;
import com.app.entities.recouvre.Bail;
import com.app.entities.recouvre.Loyann;
import com.app.enums.StatutBail;
import com.app.repositories.recouvre.BailRepository;
import com.app.repositories.recouvre.LoyannRepository;

@Service
public class DashboardService{

    @Autowired
    private BailRepository bailRepository;

    @Autowired
    private LoyannRepository loyannRepository;

    public Page<DashboardLoyerDTO> getDashboard(int annee, Pageable pageable) {

        List<Bail> baux = bailRepository.findByStatut(StatutBail.ACTIF);

        List<DashboardLoyerDTO> all = baux.stream().map(bail -> {

            DashboardLoyerDTO dto = new DashboardLoyerDTO();

            // 🔹 Nom locataire
            dto.setLocataire(
                bail.getLocataire().getNom() + " " +
                bail.getLocataire().getPrenom()
            );

            // 🔹 Pour chaque mois
            for (int mois = 1; mois <= 12; mois++) {

                Loyann l = loyannRepository
                        .findByBailAndMoisAndAnnee(bail, mois, annee)
                        .orElse(null);

                int valeur = 0;

               /* if (l != null && l.getMontantPaye() >= l.getMontantDu()) {
                    valeur = 1; // payé
                }*/
                
                if (l != null) {

                    Long montantPaye =
                            l.getMontantPaye() == null
                                    ? 0L
                                    : l.getMontantPaye();

                    Long montantDu =
                            l.getMontantDu() == null
                                    ? 0L
                                    : l.getMontantDu();

                    if (montantPaye >= montantDu) {
                        valeur = 1;
                    }
                }

                switch (mois) {
                    case 1: dto.setJan(valeur); break;
                    case 2: dto.setFev(valeur); break;
                    case 3: dto.setMar(valeur); break;
                    case 4: dto.setAvr(valeur); break;
                    case 5: dto.setMai(valeur); break;
                    case 6: dto.setJui(valeur); break;
                    case 7: dto.setJul(valeur); break;
                    case 8: dto.setAou(valeur); break;
                    case 9: dto.setSep(valeur); break;
                    case 10: dto.setOct(valeur); break;
                    case 11: dto.setNov(valeur); break;
                    case 12: dto.setDec(valeur); break;
                }
            }

            return dto;

        }).toList();

        // 🔹 Pagination manuelle (comme RecouvrementService)
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), all.size());

        List<DashboardLoyerDTO> pageContent = all.subList(start, end);

        return new PageImpl<>(pageContent, pageable, all.size());
    }
    
    
    private String normalize(String input) {
        return Normalizer.normalize(input, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase()
                .trim();
    }
    
   // FINANCIER
    public Page<DashboardLoyerMontantDTO> getDashboardMontant(int annee, String search, Integer agenceId, Pageable pageable){

    	search = (search != null && search.trim().isEmpty()) ? null : search;
    	    	
        // 🔥 Pagination réelle en base
        Page<Bail> pageBaux = bailRepository.searchBaux(search, agenceId, pageable);

        List<Bail> baux = pageBaux.getContent();

        // 🔥 Loyers de l'année (1 seule requête)
        List<Loyann> loyers = loyannRepository.findAllByAnneeWithBail(annee);

        // 🔥 Indexation
        Map<Integer, Map<Integer, Loyann>> map = new HashMap<>();

        for (Loyann l : loyers) {
            int bailId = l.getBail().getId();
            map.putIfAbsent(bailId, new HashMap<>());
            map.get(bailId).put(l.getMois(), l);
        }

        // 🔥 Transformation DTO
        List<DashboardLoyerMontantDTO> content = baux.stream()
            .map(bail -> {

                DashboardLoyerMontantDTO dto = new DashboardLoyerMontantDTO();

                dto.setLocataire(
                    bail.getLocataire().getNom() + " " +
                    bail.getLocataire().getPrenom()
                );

                long total = 0;

                Map<Integer, Loyann> moisMap =
                    map.getOrDefault(bail.getId(), new HashMap<>());

                for (int mois = 1; mois <= 12; mois++) {

                    Loyann l = moisMap.get(mois);
                    long montant = (l != null) ? l.getMontantPaye() : 0;

                    total += montant;

                    switch (mois) {
                        case 1: dto.setJan(montant); break;
                        case 2: dto.setFev(montant); break;
                        case 3: dto.setMar(montant); break;
                        case 4: dto.setAvr(montant); break;
                        case 5: dto.setMai(montant); break;
                        case 6: dto.setJui(montant); break;
                        case 7: dto.setJul(montant); break;
                        case 8: dto.setAou(montant); break;
                        case 9: dto.setSep(montant); break;
                        case 10: dto.setOct(montant); break;
                        case 11: dto.setNov(montant); break;
                        case 12: dto.setDec(montant); break;
                    }
                }

               // dto.setTotal(total);

                return dto;
            })
            .toList();

        return new PageImpl<>(content, pageable, pageBaux.getTotalElements());
    }
    
    
    public Map<String, Long> getTotals(int annee, String search) {

        List<Loyann> loyers = loyannRepository.findAllByAnneeWithBail(annee);

        Map<String, Long> totals = new HashMap<>();

        for (int i = 1; i <= 12; i++) {
            totals.put("m" + i, 0L);
        }

        long global = 0;

        for (Loyann l : loyers) {

            // 🔥 FILTRE ICI
            if (search != null && !search.isBlank()) {

                String nom = (l.getBail().getLocataire().getNom() + " " +
                              l.getBail().getLocataire().getPrenom())
                              .toLowerCase();

                String keyword = search.toLowerCase();

                if (!nom.contains(keyword)) {
                    continue; // ❌ on ignore ce loyer
                }
            }

            int mois = l.getMois();
            long montant = l.getMontantPaye();

            totals.put("m" + mois,
                totals.get("m" + mois) + montant
            );

            global += montant;
        }

        totals.put("total", global);

        return totals;
    }
        
    public List<DashboardLoyerMontantDTO> getDashboard(int annee, String search, Integer agenceId) {

        if (search != null && search.trim().isEmpty()) {
            search = null;
        }
        
        List<Bail> test = bailRepository.findAll();

        if (!test.isEmpty()) {
            Bail b = test.get(0);

            System.out.println("LOCATAIRE CLASS = " + b.getLocataire().getClass());
            System.out.println("LOCATAIRE NAME CLASS = " + b.getLocataire().getNom().getClass());
        }

        // 🔥 Tous les baux (sans pagination)
        List<Bail> baux = bailRepository.searchBauxNoPage(search,agenceId);

        // 🔥 Tous les loyers de l'année
        List<Loyann> loyers = loyannRepository.findAllByAnneeWithBail(annee);

        // 🔥 Indexation
        Map<Integer, Map<Integer, Loyann>> map = new HashMap<>();

        for (Loyann l : loyers) {
            int bailId = l.getBail().getId();
            map.putIfAbsent(bailId, new HashMap<>());
            map.get(bailId).put(l.getMois(), l);
        }

        // 🔥 Transformation DTO
        return baux.stream()
            .map(bail -> {

                DashboardLoyerMontantDTO dto = new DashboardLoyerMontantDTO();

                dto.setLocataire(
                    bail.getLocataire().getNom() + " " +
                    bail.getLocataire().getPrenom()
                );

                long total = 0;

                Map<Integer, Loyann> moisMap =
                    map.getOrDefault(bail.getId(), new HashMap<>());

                for (int mois = 1; mois <= 12; mois++) {

                    Loyann l = moisMap.get(mois);
                    long montant = (l != null) ? l.getMontantPaye() : 0;

                    total += montant;

                    switch (mois) {
                        case 1: dto.setJan(montant); break;
                        case 2: dto.setFev(montant); break;
                        case 3: dto.setMar(montant); break;
                        case 4: dto.setAvr(montant); break;
                        case 5: dto.setMai(montant); break;
                        case 6: dto.setJui(montant); break;
                        case 7: dto.setJul(montant); break;
                        case 8: dto.setAou(montant); break;
                        case 9: dto.setSep(montant); break;
                        case 10: dto.setOct(montant); break;
                        case 11: dto.setNov(montant); break;
                        case 12: dto.setDec(montant); break;
                    }
                }

               // dto.setTotal(total);

                return dto;
            })
            .toList();
    }
}
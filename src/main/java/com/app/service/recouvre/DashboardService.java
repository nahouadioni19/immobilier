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
public class DashboardService {

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

                if (l != null && l.getMontantPaye() >= l.getMontantDu()) {
                    valeur = 1; // payé
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
    public Page<DashboardLoyerMontantDTO> getDashboardMontant(int annee, String search, Pageable pageable){

		/*
		 * if (search != null && search.trim().isEmpty()) { search = null; }
		 */
    	
    	search = (search != null && search.trim().isEmpty()) ? null : search;

        // 🔥 Pagination réelle en base
        Page<Bail> pageBaux = bailRepository.searchBaux(search, pageable);

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
        
    public List<DashboardLoyerMontantDTO> getDashboard(int annee, String search) {

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
        List<Bail> baux = bailRepository.searchBauxNoPage(search);

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
    
    /*public Page<DashboardLoyerMontantDTO> getDashboardMontant(int annee, String search, Pageable pageable){

        List<Bail> baux = bailRepository.findByStatut(StatutBail.ACTIF);

        List<DashboardLoyerMontantDTO> all = baux.stream()

            // 🔍 FILTRE PAR NOM
            .filter(b -> {
                if (search == null || search.isBlank()) return true;

                String nom = (b.getLocataire().getNom() + " " +
                              b.getLocataire().getPrenom()).toLowerCase();

                return nom.contains(search.toLowerCase());
            })

            .map(bail -> {

                DashboardLoyerMontantDTO dto = new DashboardLoyerMontantDTO();

                dto.setLocataire(
                    bail.getLocataire().getNom() + " " +
                    bail.getLocataire().getPrenom()
                );

                long total = 0;

                for (int mois = 1; mois <= 12; mois++) {

                    Loyann l = loyannRepository
                            .findByBailAndMoisAndAnnee(bail, mois, annee)
                            .orElse(null);

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

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), all.size());

        return new PageImpl<>(all.subList(start, end), pageable, all.size());
    }*/
    
	/*
	 * public Page<DashboardLoyerMontantDTO> getDashboardMontant(int annee, Pageable
	 * pageable){
	 * 
	 * List<Bail> baux = bailRepository.findByStatut(StatutBail.ACTIF);
	 * 
	 * List<DashboardLoyerMontantDTO> all = baux.stream().map(bail -> {
	 * 
	 * DashboardLoyerMontantDTO dto = new DashboardLoyerMontantDTO();
	 * 
	 * // 🔹 Locataire dto.setLocataire( bail.getLocataire().getNom() + " " +
	 * bail.getLocataire().getPrenom() );
	 * 
	 * long total = 0;
	 * 
	 * // 🔹 Boucle sur les 12 mois for (int mois = 1; mois <= 12; mois++) {
	 * 
	 * Loyann l = loyannRepository .findByBailAndMoisAndAnnee(bail, mois, annee)
	 * .orElse(null);
	 * 
	 * long montant = 0;
	 * 
	 * if (l != null) { montant = l.getMontantPaye(); // 🔥 montant payé réel }
	 * 
	 * total += montant;
	 * 
	 * switch (mois) { case 1: dto.setJan(montant); break; case 2:
	 * dto.setFev(montant); break; case 3: dto.setMar(montant); break; case 4:
	 * dto.setAvr(montant); break; case 5: dto.setMai(montant); break; case 6:
	 * dto.setJui(montant); break; case 7: dto.setJul(montant); break; case 8:
	 * dto.setAou(montant); break; case 9: dto.setSep(montant); break; case 10:
	 * dto.setOct(montant); break; case 11: dto.setNov(montant); break; case 12:
	 * dto.setDec(montant); break; } }
	 * 
	 * // dto.setTotal(total);
	 * 
	 * return dto;
	 * 
	 * }).toList();
	 * 
	 * // 🔹 Pagination manuelle (comme RecouvrementService) int start = (int)
	 * pageable.getOffset(); int end = Math.min(start + pageable.getPageSize(),
	 * all.size());
	 * 
	 * List<DashboardLoyerMontantDTO> pageContent = all.subList(start, end);
	 * 
	 * return new PageImpl<>(pageContent, pageable, all.size()); }
	 */
}


//13/04/2026

/*@Service
public class DashboardService {

    @Autowired
    private LoyannRepository repo;
    
    //BINAIRE
    public Page<DashboardLoyerDTO> getDashboard(int annee, Pageable pageable) {

        List<Loyann> loyers = repo.findByAnnee(annee);

        Map<Integer, DashboardLoyerDTO> map = new HashMap<>();

        for (Loyann l : loyers) {

            Integer bailId = l.getBail().getId();
            map.putIfAbsent(bailId, new DashboardLoyerDTO());
            DashboardLoyerDTO dto = map.get(bailId);

            // Locataire
            dto.setLocataire(
                l.getBail().getLocataire().getNom() + " " + l.getBail().getLocataire().getPrenom()
            );

            // Marquer le mois payé
            switch (l.getMois()) {
                case 1: dto.setJan(1); break;
                case 2: dto.setFev(1); break;
                case 3: dto.setMar(1); break;
                case 4: dto.setAvr(1); break;
                case 5: dto.setMai(1); break;
                case 6: dto.setJui(1); break;
                case 7: dto.setJul(1); break;
                case 8: dto.setAou(1); break;
                case 9: dto.setSep(1); break;
                case 10: dto.setOct(1); break;
                case 11: dto.setNov(1); break;
                case 12: dto.setDec(1); break;
            }
        }

      //  return new ArrayList<>(map.values());
        return repo.getDashboard(annee, pageable);
    }
    
 //FINANCIER
    public Page<DashboardLoyerMontantDTO> getDashboardMontant(int annee, Pageable pageable){
    	
    	return repo.getDashboardMontant(annee, pageable);
    	
    }
}*/


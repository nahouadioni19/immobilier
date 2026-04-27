package com.app.service.recouvre;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import com.app.entities.recouvre.Bail;
import com.app.entities.recouvre.Loyann;
import com.app.repositories.recouvre.BailRepository;
import com.app.repositories.recouvre.LoyannRepository;
import com.app.service.base.BaseService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class LoyannService extends BaseService<Loyann>{

    private final LoyannRepository repo;
    private final BailRepository bailRepository;

    @Override
    public JpaRepository<Loyann, Integer> getRepository() {
        return repo;
    }

    public List<Loyann> saveAll(List<Loyann> loyers) {
        return repo.saveAll(loyers);
    }

    public Loyann getDernierLoyer(Bail bail) {
        return repo.findTopByBailOrderByAnneeDescMoisDesc(bail);
    }
    
    /**
     * Répartir un paiement sur les mois du bail.
     */
    public List<Loyann> repartirLoyerAncien(Bail bail, long montantPaye, long loyerMensuel, LocalDate dateDebut) {
        List<Loyann> loyers = new ArrayList<>();
        long reste = montantPaye;

        // 1. Vérifier s'il existe déjà un dernier mois payé ou partiellement payé
        Loyann dernierExist = repo.findTopByBailOrderByAnneeDescMoisDesc(bail);
        LocalDate current;

        if (dernierExist != null) {
            // Reprendre à partir du dernier mois enregistré
            current = LocalDate.of(dernierExist.getAnnee(), dernierExist.getMois(), 1);

            if (dernierExist.getLoyer() < loyerMensuel) {
                long manque = loyerMensuel - dernierExist.getLoyer();

                if (reste >= manque) {
                    // Compléter le mois en cours
                    dernierExist.setLoyer(loyerMensuel);
                    repo.save(dernierExist);

                    reste -= manque;
                    loyers.add(dernierExist);

                    // Passer au mois suivant
                    current = current.plusMonths(1);
                } else {
                    // On ne peut compléter que partiellement
                    dernierExist.setLoyer(dernierExist.getLoyer() + reste);
                    repo.save(dernierExist);

                    loyers.add(dernierExist);

                    // Mise à jour bail.dateFin = mois du dernier paiement partiel + 1
                    LocalDate fin = LocalDate.of(dernierExist.getAnnee(), dernierExist.getMois(), 1).plusMonths(1);
                    bail.setDateFin(fin);
                    bailRepository.save(bail);

                    return loyers; // tout est consommé
                }
            } else {
                // Le dernier mois était complet → on commence au suivant
                current = current.plusMonths(1);
            }
        } else {
            // Aucun enregistrement précédent → commencer à la dateDebut
            current = dateDebut;
        }

        // 2. Répartir le reste du paiement mois par mois
        while (reste > 0) {
            Loyann l = new Loyann();
            l.setBail(bail);
            l.setMois(current.getMonthValue());
            l.setAnnee(current.getYear());

            if (reste >= loyerMensuel) {
                l.setLoyer(loyerMensuel);
                reste -= loyerMensuel;
            } else {
                l.setLoyer(reste); // partiel
                reste = 0;
            }

            repo.save(l);
            loyers.add(l);

            current = current.plusMonths(1);
        }

        // 3. Mise à jour de la date_fin du bail
        if (!loyers.isEmpty()) {
            Loyann dernierCree = loyers.get(loyers.size() - 1);
            LocalDate fin = LocalDate.of(dernierCree.getAnnee(), dernierCree.getMois(), 1).plusMonths(1);
            bail.setDateFin(fin);
            bailRepository.save(bail);
        }

        return loyers;
    }
        
    /*public List<Loyann> repartirLoyer(Bail bail, long montantPaye, long loyerMensuel, LocalDate dateDebut) {

        List<Loyann> loyers = new ArrayList<>();
        long reste = montantPaye;

        Loyann dernierExist = repo.findTopByBailOrderByAnneeDescMoisDesc(bail);
        LocalDate current;

        if (dernierExist != null) {

            current = LocalDate.of(dernierExist.getAnnee(), dernierExist.getMois(), 1);

            long montantDu = dernierExist.getMontantDu() != null ? dernierExist.getMontantDu() : loyerMensuel;
            long montantPayeExistant = dernierExist.getMontantPaye() != null
                    ? dernierExist.getMontantPaye()
                    : dernierExist.getLoyer();

            if (montantPayeExistant < montantDu) {

                long manque = montantDu - montantPayeExistant;

                if (reste >= manque) {

                    // ✅ Compléter le mois
                    dernierExist.setMontantDu(montantDu);
                    dernierExist.setMontantPaye(montantDu);
                    dernierExist.setLoyer(montantDu);

                    // 🔥 STATUT
                    dernierExist.setStatut("PAYE");
                    dernierExist.setAgence(getCurrentAgence());
                    repo.save(dernierExist);

                    reste -= manque;
                    loyers.add(dernierExist);

                    current = current.plusMonths(1);

                } else {

                    // ✅ Paiement partiel
                    long nouveauMontant = montantPayeExistant + reste;

                    dernierExist.setMontantDu(montantDu);
                    dernierExist.setMontantPaye(nouveauMontant);
                    dernierExist.setLoyer(nouveauMontant);

                    // 🔥 STATUT
                    if (LocalDate.now().getDayOfMonth() > 10) {
                        dernierExist.setStatut("RETARD");
                    } else {
                        dernierExist.setStatut("PARTIEL");
                    }
                    
                    dernierExist.setAgence(getCurrentAgence());
                    repo.save(dernierExist);

                    loyers.add(dernierExist);

                    LocalDate fin = current.plusMonths(1);
                    bail.setDateFin(fin);
                    bailRepository.save(bail);

                    return loyers;
                }

            } else {
                current = current.plusMonths(1);
            }

        } else {
            current = dateDebut;
        }

        // =========================
        // 🔥 NOUVEAUX MOIS
        // =========================

        while (reste > 0) {

            Loyann l = new Loyann();
            l.setBail(bail);
            l.setMois(current.getMonthValue());
            l.setAnnee(current.getYear());

            l.setMontantDu(loyerMensuel);

            if (reste >= loyerMensuel) {

                l.setMontantPaye(loyerMensuel);
                l.setLoyer(loyerMensuel);

                // 🔥 STATUT
                l.setStatut("PAYE");

                reste -= loyerMensuel;

            } else {

                l.setMontantPaye(reste);
                l.setLoyer(reste);

                // 🔥 STATUT
                if (LocalDate.now().getDayOfMonth() > 10) {
                    l.setStatut("RETARD");
                } else {
                    l.setStatut("PARTIEL");
                }

                reste = 0;
            }
            
            l.setAgence(getCurrentAgence());
            repo.save(l);
            loyers.add(l);

            current = current.plusMonths(1);
        }

        // =========================
        // 🔥 MAJ DATE FIN
        // =========================

        if (!loyers.isEmpty()) {
            Loyann dernierCree = loyers.get(loyers.size() - 1);
            LocalDate fin = LocalDate.of(dernierCree.getAnnee(), dernierCree.getMois(), 1).plusMonths(1);
            bail.setDateFin(fin);
            bailRepository.save(bail);
        }

        return loyers;
    }*/
    
    @Transactional
    public List<Loyann> repartirLoyer(Bail bail, long montantPaye, long loyerMensuel, LocalDate datePaiement) {

        if (loyerMensuel <= 0) {
            throw new IllegalArgumentException("Loyer mensuel invalide");
        }

        if (!bail.getAgence().getId().equals(getCurrentAgence().getId())) {
            throw new SecurityException("Bail d’une autre agence");
        }

        List<Loyann> loyers = new ArrayList<>();
        List<Loyann> toSave = new ArrayList<>();

        long reste = montantPaye;

        Loyann dernierExist = repo.findTopByBailOrderByAnneeDescMoisDesc(bail);
        LocalDate current;

        if (dernierExist != null) {

            current = LocalDate.of(dernierExist.getAnnee(), dernierExist.getMois(), 1);

            long montantDu = Optional.ofNullable(dernierExist.getMontantDu()).orElse(loyerMensuel);
            long montantPayeExistant = Optional.ofNullable(dernierExist.getMontantPaye()).orElse(0L);

            if (montantPayeExistant < montantDu) {

                long manque = montantDu - montantPayeExistant;

                if (reste >= manque) {

                    dernierExist.setMontantPaye(montantDu);
                    dernierExist.setStatut("PAYE");

                    reste -= manque;
                    loyers.add(dernierExist);
                    toSave.add(dernierExist);

                    current = current.plusMonths(1);

                } else {

                    dernierExist.setMontantPaye(montantPayeExistant + reste);

                    dernierExist.setStatut(
                            datePaiement.getDayOfMonth() > 10 ? "RETARD" : "PARTIEL"
                    );

                    toSave.add(dernierExist);
                    loyers.add(dernierExist);

                    bail.setDateFin(current.plusMonths(1));
                    bailRepository.save(bail);

                    repo.saveAll(toSave);
                    return loyers;
                }

            } else {
                current = current.plusMonths(1);
            }

        } else {
            current = datePaiement != null ? datePaiement : LocalDate.now();
        }

        int count = 0;

        while (reste > 0 && count < 120) {
            count++;

            Loyann l = new Loyann();
            l.setBail(bail);
            l.setMois(current.getMonthValue());
            l.setAnnee(current.getYear());
            l.setMontantDu(loyerMensuel);

            if (reste >= loyerMensuel) {
                l.setMontantPaye(loyerMensuel);
                l.setStatut("PAYE");
                reste -= loyerMensuel;
            } else {
                l.setMontantPaye(reste);
                l.setStatut(datePaiement.getDayOfMonth() > 10 ? "RETARD" : "PARTIEL");
                reste = 0;
            }

            l.setAgence(getCurrentAgence());

            toSave.add(l);
            loyers.add(l);

            current = current.plusMonths(1);
        }

        repo.saveAll(toSave);

        if (!loyers.isEmpty()) {
            Loyann dernier = loyers.get(loyers.size() - 1);
            LocalDate fin = LocalDate.of(dernier.getAnnee(), dernier.getMois(), 1).plusMonths(1);
            bail.setDateFin(fin);
            bailRepository.save(bail);
        }

        return loyers;
    }
    
}


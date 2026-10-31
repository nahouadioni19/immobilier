package com.app.service.recouvre;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import com.app.entities.administration.Agence;
import com.app.entities.recouvre.Bail;
import com.app.entities.recouvre.Encaisse;
import com.app.entities.recouvre.Loyann;
import com.app.repositories.recouvre.AffectationEncaissementRepository;
import com.app.repositories.recouvre.BailRepository;
import com.app.repositories.recouvre.LoyannRepository;
import com.app.service.base.BaseService;

import java.util.Objects;

import com.app.entities.recouvre.AffectationEncaissement;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class LoyannService extends BaseService<Loyann>{

    private final LoyannRepository repo;
    private final BailRepository bailRepository;
    private final AffectationEncaissementRepository affectationRepository;
    private final RevisionLoyerService revisionLoyerService;    

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
    /**public List<Loyann> repartirLoyerAncien(Bail bail, long montantPaye, long loyerMensuel, LocalDate dateDebut) {
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
    }*/

    
    /**@Transactional
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
                    bail.setDerniereDatePaiement(current.plusMonths(1));
                    
                    bail.setDerniereDatePaiement(
                            encaisse.getEncDate() != null
                                    ? encaisse.getEncDate()
                                    : LocalDate.now());
                    
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
            bail.setDerniereDatePaiement(fin);
            bailRepository.save(bail);
        }

        return loyers;
    }*/
    
    
    /**
     * Affecte un paiement aux échéances les plus anciennes.
     *
     * Les échéances sont créées uniquement lorsqu'un paiement
     * validé permet de commencer à les couvrir.
     *
     * Cette méthode doit être appelée dans la transaction
     * de validation de l'encaissement.
     */
    /**public List<Loyann> affecterPaiement(
            Encaisse encaisse,
            Bail bail,
            long montantAPayer,
            LocalDate datePaiement) {

        if (encaisse == null || bail == null) {
            throw new IllegalArgumentException(
                    "L'encaissement et le bail sont obligatoires.");
        }

        if (montantAPayer < 0) {
            throw new IllegalArgumentException(
                    "Le montant à affecter ne peut pas être négatif.");
        }

        if (montantAPayer == 0) {
            return List.of();
        }

        if (bail.getDateDebut() == null) {
            throw new IllegalStateException(
                    "La date de début du bail est obligatoire.");
        }

        Agence agence = encaisse.getAgence();

        if (agence == null
                || bail.getAgence() == null
                || !Objects.equals(
                        agence.getId(), bail.getAgence().getId())) {
            throw new SecurityException(
                    "L'encaissement et le bail doivent appartenir "
                  + "à la même agence.");
        }

        if (datePaiement == null) {
            datePaiement = LocalDate.now();
        }

        long reste = montantAPayer;

        LocalDate moisCourant = bail.getDateDebut().withDayOfMonth(1);

        List<Loyann> echeancesTraitees = new ArrayList<>();

        final int plafondMois = 600;
        int compteur = 0;

        while (reste > 0 && compteur < plafondMois) {
            compteur++;

            int mois = moisCourant.getMonthValue();
            int annee = moisCourant.getYear();

            Loyann echeance = repo.trouverPourMiseAJour(
                    bail.getId(), annee, mois).orElse(null);

            if (echeance == null) {
                long montantDu = revisionLoyerService
                        .montantApplicable(bail, moisCourant);

                echeance = new Loyann();
                echeance.setBail(bail);
                echeance.setAgence(agence);
                echeance.setMois(mois);
                echeance.setAnnee(annee);
                echeance.setLoyer(montantDu);
                echeance.setMontantDu(montantDu);
                echeance.setMontantPaye(0L);
                echeance.setPenalite(0L);
                echeance.setStatut("IMPAYE");
            } else {
                // Sécurité : ne jamais modifier une échéance d'une autre agence.
                if (echeance.getAgence() == null
                        || !Objects.equals(
                                echeance.getAgence().getId(),
                                agence.getId())) {
                    throw new SecurityException(
                            "Une échéance appartient à une autre agence.");
                }
            }

            long montantDu = echeance.getMontantDuSafe();
            long montantDejaPaye = echeance.getMontantPayeSafe();

            if (montantDu <= 0) {
                throw new IllegalStateException(
                        "Le montant dû de l'échéance "
                      + mois + "/" + annee + " est invalide.");
            }

            if (montantDejaPaye < 0 || montantDejaPaye > montantDu) {
                throw new IllegalStateException(
                        "Le montant payé de l'échéance "
                      + mois + "/" + annee + " est incohérent.");
            }

            long soldeEcheance = montantDu - montantDejaPaye;

            if (soldeEcheance == 0) {
                moisCourant = moisCourant.plusMonths(1);
                continue;
            }

            long montantAffecte = Math.min(reste, soldeEcheance);

            echeance.setMontantDu(montantDu);
            echeance.setLoyer(montantDu);
            echeance.setMontantPaye(montantDejaPaye + montantAffecte);

            if (echeance.getMontantPaye() >= montantDu) {
                echeance.setStatut("PAYE");
            } else if (echeance.getMontantPaye() > 0) {
                echeance.setStatut("PARTIEL");
            } else {
                echeance.setStatut("IMPAYE");
            }

            echeance = repo.saveAndFlush(echeance);

            AffectationEncaissement affectation =
                    new AffectationEncaissement();

            affectation.setEncaisse(encaisse);
            affectation.setLoyann(echeance);
            affectation.setAgence(agence);
            affectation.setMontantAffecte(montantAffecte);

            affectationRepository.save(affectation);

            echeancesTraitees.add(echeance);

            reste -= montantAffecte;

            if (echeance.getMontantPaye() >= montantDu) {
                moisCourant = moisCourant.plusMonths(1);
            }
        }

        if (reste > 0) {
            throw new IllegalStateException(
                    "Le montant n'a pas pu être entièrement affecté. "
                  + "La transaction doit être annulée.");
        }

        return echeancesTraitees;
    }*/
    
    
    /*public List<Loyann> affecterPaiement(
            Encaisse encaisse,
            Bail bail,
            long montantAPayer,
            LocalDate datePaiement) {

        if (encaisse == null || bail == null) {
            throw new IllegalArgumentException(
                    "L'encaissement et le bail sont obligatoires.");
        }

        if (montantAPayer < 0) {
            throw new IllegalArgumentException(
                    "Le montant à affecter ne peut pas être négatif.");
        }

        if (montantAPayer == 0) {
            return List.of();
        }

        if (bail.getDateDebut() == null) {
            throw new IllegalStateException(
                    "La date de début du bail est obligatoire.");
        }

        Agence agence = encaisse.getAgence();

        if (agence == null
                || bail.getAgence() == null
                || !Objects.equals(
                        agence.getId(), bail.getAgence().getId())) {
            throw new SecurityException(
                    "L'encaissement et le bail doivent appartenir "
                  + "à la même agence.");
        }

        if (datePaiement == null) {
            datePaiement = LocalDate.now();
        }

        long reste = montantAPayer;

        
        LocalDate moisCourant;

        if (bail.getDateFinRecouvre() != null) {
            moisCourant = bail.getDateFinRecouvre()
                    .withDayOfMonth(1)
                    .plusMonths(1);
        } else {
            moisCourant = bail.getDateDebut().withDayOfMonth(1);
        }

        LocalDate dernierMoisSolde = null;

        List<Loyann> echeancesTraitees = new ArrayList<>();

        final int plafondMois = 600;
        int compteur = 0;

        while (reste > 0 && compteur < plafondMois) {
            compteur++;

            int mois = moisCourant.getMonthValue();
            int annee = moisCourant.getYear();

            Loyann echeance = repo.trouverPourMiseAJour(
                    bail.getId(), annee, mois).orElse(null);

            if (echeance == null) {
                // montantApplicable tient compte des révisions de loyer.
                long montantDu = revisionLoyerService
                        .montantApplicable(bail, moisCourant);

                echeance = new Loyann();
                echeance.setBail(bail);
                echeance.setAgence(agence);
                echeance.setMois(mois);
                echeance.setAnnee(annee);
                echeance.setLoyer(montantDu);
                echeance.setMontantDu(montantDu);
                echeance.setMontantPaye(0L);
                echeance.setPenalite(0L);
                echeance.setStatut("IMPAYE");
            } else {
                // Sécurité : ne jamais modifier une échéance d'une autre agence.
                if (echeance.getAgence() == null
                        || !Objects.equals(
                                echeance.getAgence().getId(),
                                agence.getId())) {
                    throw new SecurityException(
                            "Une échéance appartient à une autre agence.");
                }
            }

            
            long montantDu = echeance.getMontantDuSafe();
            long montantDejaPaye = echeance.getMontantPayeSafe();

            if (montantDu <= 0) {
                throw new IllegalStateException(
                        "Le montant dû de l'échéance "
                      + mois + "/" + annee + " est invalide.");
            }

            if (montantDejaPaye < 0 || montantDejaPaye > montantDu) {
                throw new IllegalStateException(
                        "Le montant payé de l'échéance "
                      + mois + "/" + annee + " est incohérent.");
            }

            long soldeEcheance = montantDu - montantDejaPaye;

            if (soldeEcheance == 0) {
                // Mois déjà soldé : il fait partie de la période recouvrée.
                dernierMoisSolde = moisCourant;
                moisCourant = moisCourant.plusMonths(1);
                continue;
            }

            long montantAffecte = Math.min(reste, soldeEcheance);

            echeance.setMontantDu(montantDu);
            echeance.setLoyer(montantDu);
            echeance.setMontantPaye(montantDejaPaye + montantAffecte);

            if (echeance.getMontantPaye() >= montantDu) {
                echeance.setStatut("PAYE");
            } else if (echeance.getMontantPaye() > 0) {
                echeance.setStatut("PARTIEL");
            } else {
                echeance.setStatut("IMPAYE");
            }

            echeance = repo.saveAndFlush(echeance);

            AffectationEncaissement affectation =
                    new AffectationEncaissement();

            affectation.setEncaisse(encaisse);
            affectation.setLoyann(echeance);
            affectation.setAgence(agence);
            affectation.setMontantAffecte(montantAffecte);

            affectationRepository.save(affectation);

            echeancesTraitees.add(echeance);

            reste -= montantAffecte;

            if (echeance.getMontantPaye() >= montantDu) {
                dernierMoisSolde = moisCourant;
                moisCourant = moisCourant.plusMonths(1);
            }
        }

        if (reste > 0) {
            throw new IllegalStateException(
                    "Le montant n'a pas pu être entièrement affecté. "
                  + "La transaction doit être annulée.");
        }

        if (dernierMoisSolde != null) {
            LocalDate nouvelleFin = dernierMoisSolde
                    .withDayOfMonth(dernierMoisSolde.lengthOfMonth());

            if (bail.getDateFinRecouvre() == null
                    || nouvelleFin.isAfter(bail.getDateFinRecouvre())) {
                bail.setDateFinRecouvre(nouvelleFin);
            }
        }

        return echeancesTraitees;
    }*/
    
    
    public List<Loyann> affecterPaiement(
            Encaisse encaisse,
            Bail bail,
            long montantAPayer,
            LocalDate datePaiement) {

        if (encaisse == null || bail == null) {
            throw new IllegalArgumentException(
                    "L'encaissement et le bail sont obligatoires.");
        }

        if (montantAPayer < 0) {
            throw new IllegalArgumentException(
                    "Le montant à affecter ne peut pas être négatif.");
        }

        if (montantAPayer == 0) {
            return List.of();
        }

        if (bail.getDateDebut() == null) {
            throw new IllegalStateException(
                    "La date de début du bail est obligatoire.");
        }

        Agence agence = encaisse.getAgence();

        if (agence == null
                || bail.getAgence() == null
                || !Objects.equals(
                        agence.getId(), bail.getAgence().getId())) {
            throw new SecurityException(
                    "L'encaissement et le bail doivent appartenir "
                  + "à la même agence.");
        }

        if (datePaiement == null) {
            datePaiement = LocalDate.now();
        }

        long reste = montantAPayer;

        // Point de départ : le mois qui suit la période déjà recouvrée,
        // sinon le mois de début du bail.
        LocalDate moisCourant;

        if (bail.getDateFinRecouvre() != null) {
            moisCourant = bail.getDateFinRecouvre()
                    .withDayOfMonth(1)
                    .plusMonths(1);
        } else {
            moisCourant = bail.getDateDebut().withDayOfMonth(1);
        }

        LocalDate dernierMoisSolde = null;

        List<Loyann> echeancesTraitees = new ArrayList<>();

        final int plafondMois = 600;
        int compteur = 0;

        while (reste > 0 && compteur < plafondMois) {
            compteur++;

            int mois = moisCourant.getMonthValue();
            int annee = moisCourant.getYear();

            Loyann echeance = repo.trouverPourMiseAJour(
                    bail.getId(), annee, mois).orElse(null);

            if (echeance == null) {
                long montantDuNouveau = determinerLoyerDu(bail, moisCourant);

                if (montantDuNouveau <= 0) {
                    throw new IllegalStateException(
                            "Impossible de déterminer le loyer dû pour "
                          + mois + "/" + annee
                          + " (bail " + bail.getId() + ").");
                }

                echeance = new Loyann();
                echeance.setBail(bail);
                echeance.setAgence(agence);
                echeance.setMois(mois);
                echeance.setAnnee(annee);
                echeance.setLoyer(montantDuNouveau);
                echeance.setMontantDu(montantDuNouveau);
                echeance.setMontantPaye(0L);
                echeance.setPenalite(0L);
                echeance.setStatut("IMPAYE");
            } else {
                if (echeance.getAgence() == null
                        || !Objects.equals(
                                echeance.getAgence().getId(),
                                agence.getId())) {
                    throw new SecurityException(
                            "Une échéance appartient à une autre agence.");
                }
            }

            long montantDu = echeance.getMontantDuSafe();
            long montantDejaPaye = echeance.getMontantPayeSafe();

            // Anciennes échéances (avant la révision) : montantDu non renseigné.
            if (montantDu <= 0) {
                long reference = 0L;

                Long loyerEcheance = echeance.getLoyer();
                if (loyerEcheance != null && loyerEcheance > 0) {
                    reference = loyerEcheance;
                }

                if (reference <= 0) {
                    reference = determinerLoyerDu(bail, moisCourant);
                }

                if (reference <= 0) {
                    throw new IllegalStateException(
                            "Impossible de déterminer le loyer dû pour "
                          + mois + "/" + annee
                          + " (bail " + bail.getId() + ").");
                }

                echeance.setMontantDu(reference);
                echeance.setLoyer(reference);
                montantDu = reference;
            }

            if (montantDejaPaye < 0) {
                throw new IllegalStateException(
                        "Le montant payé de l'échéance "
                      + mois + "/" + annee + " est négatif.");
            }

            // Payé >= dû : le mois est considéré comme soldé, on ne bloque plus.
            long soldeEcheance = Math.max(0L, montantDu - montantDejaPaye);

            if (soldeEcheance == 0) {
                dernierMoisSolde = moisCourant;
                moisCourant = moisCourant.plusMonths(1);
                continue;
            }

            long montantAffecte = Math.min(reste, soldeEcheance);

            echeance.setMontantDu(montantDu);
            echeance.setLoyer(montantDu);
            echeance.setMontantPaye(montantDejaPaye + montantAffecte);

            if (echeance.getMontantPaye() >= montantDu) {
                echeance.setStatut("PAYE");
            } else if (echeance.getMontantPaye() > 0) {
                echeance.setStatut("PARTIEL");
            } else {
                echeance.setStatut("IMPAYE");
            }

            echeance = repo.saveAndFlush(echeance);

            AffectationEncaissement affectation = new AffectationEncaissement();
            affectation.setEncaisse(encaisse);
            affectation.setLoyann(echeance);
            affectation.setAgence(agence);
            affectation.setMontantAffecte(montantAffecte);

            affectationRepository.save(affectation);

            echeancesTraitees.add(echeance);

            reste -= montantAffecte;

            if (echeance.getMontantPaye() >= montantDu) {
                dernierMoisSolde = moisCourant;
                moisCourant = moisCourant.plusMonths(1);
            }
        }

        if (reste > 0) {
            throw new IllegalStateException(
                    "Le montant n'a pas pu être entièrement affecté "
                  + "(reste " + reste + " FCFA, bail " + bail.getId()
                  + "). La transaction est annulée.");
        }

        if (dernierMoisSolde != null) {
            LocalDate nouvelleFin = dernierMoisSolde
                    .withDayOfMonth(dernierMoisSolde.lengthOfMonth());

            if (bail.getDateFinRecouvre() == null
                    || nouvelleFin.isAfter(bail.getDateFinRecouvre())) {
                bail.setDateFinRecouvre(nouvelleFin);
            }
        }

        return echeancesTraitees;
    }

    /**
     * Loyer dû pour un mois : tient compte des révisions, et retombe
     * sur le loyer de base du bail si aucune révision ne s'applique.
     */
    private long determinerLoyerDu(Bail bail, LocalDate mois) {
        long montant = revisionLoyerService.montantApplicable(bail, mois);

        if (montant <= 0) {
            Long loyerBail = bail.getMontantLoyer();
            if (loyerBail != null && loyerBail > 0) {
                montant = loyerBail;
            }
        }
        return montant;
    }

    
}


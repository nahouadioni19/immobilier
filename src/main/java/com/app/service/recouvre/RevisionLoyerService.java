package com.app.service.recouvre;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.dto.revision.RevisionLoyerDto;
import com.app.entities.administration.Agence;
import com.app.entities.administration.Utilisateur;
import com.app.entities.recouvre.Bail;
import com.app.entities.recouvre.Loyann;
import com.app.entities.recouvre.RevisionLoyer;
import com.app.repositories.recouvre.BailRepository;
import com.app.repositories.recouvre.LoyannRepository;
import com.app.repositories.recouvre.RevisionLoyerRepository;
import com.app.service.base.BaseService;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class RevisionLoyerService extends BaseService<RevisionLoyer> {

    private final RevisionLoyerRepository revisionLoyerRepository;
    private final BailRepository bailRepository;
    private final LoyannRepository loyannRepository;

    @Override
    public JpaRepository<RevisionLoyer, Integer> getRepository() {
        return revisionLoyerRepository;
    }

    public RevisionLoyer reviserLoyer(
            Integer bailId,
            Long nouveauMontant,
            LocalDate dateEffet,
            String motif,
            Utilisateur utilisateur) {

        if (bailId == null) {
            throw new IllegalArgumentException("Le bail est obligatoire.");
        }

        if (nouveauMontant == null || nouveauMontant <= 0) {
            throw new IllegalArgumentException(
                    "Le nouveau loyer doit être supérieur à zéro.");
        }

        if (dateEffet == null || dateEffet.getDayOfMonth() != 1) {
            throw new IllegalArgumentException(
                    "La date d'effet doit être le premier jour d'un mois.");
        }

        Agence agence = getCurrentAgence();

        Bail bail = bailRepository.trouverPourMiseAJour(bailId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Bail introuvable."));

        verifierAgence(bail.getAgence(), agence.getId());

        if (bail.getDateDebut() != null
                && dateEffet.isBefore(
                        bail.getDateDebut().withDayOfMonth(1))) {
            throw new IllegalArgumentException(
                    "La révision ne peut pas précéder le début du bail.");
        }

        if (revisionLoyerRepository.existsByBail_IdAndDateEffet(bailId, dateEffet)) {
            throw new IllegalArgumentException(
                    "Une révision existe déjà pour ce bail à cette date.");
        }

        Loyann derniereEcheance =
                loyannRepository.findTopByBailOrderByAnneeDescMoisDesc(bail);

        if (derniereEcheance != null) {
            LocalDate debutDerniereEcheance = LocalDate.of(
                    derniereEcheance.getAnnee(),
                    derniereEcheance.getMois(),
                    1);

            if (!dateEffet.isAfter(debutDerniereEcheance)) {
                throw new IllegalArgumentException(
                        "La date d'effet doit être postérieure au dernier "
                      + "mois déjà enregistré dans Loyann. "
                      + "Une échéance historique ne sera pas recalculée.");
            }
        }

        long ancienMontant = bail.getMontantLoyer() == null
                ? 0L
                : bail.getMontantLoyer();

        if (ancienMontant <= 0) {
            throw new IllegalArgumentException(
                    "Le loyer actuel du bail est invalide.");
        }

        RevisionLoyer revision = new RevisionLoyer();
        revision.setBail(bail);
        revision.setAgence(agence);
        revision.setAncienMontant(ancienMontant);
        revision.setNouveauMontant(nouveauMontant);
        revision.setDateEffet(dateEffet);
        revision.setMotif(motif == null ? null : motif.trim());
        revision.setUtilisateur(utilisateur);

        revisionLoyerRepository.save(revision);

        // Le loyer courant du bail devient le nouveau montant.
        // Les anciennes échéances Loyann ne sont pas modifiées.
        bail.setMontantLoyer(nouveauMontant);
        bailRepository.save(bail);

        return revision;
    }

    /**
     * Retourne le montant applicable au mois demandé.
     * Les dates d'effet sont obligatoirement au premier du mois.
     */
    
    /*@Transactional(readOnly = true)
    public long montantApplicable(Bail bail, LocalDate mois) {

        if (bail == null || mois == null) {
            throw new IllegalArgumentException(
                    "Le bail et le mois sont obligatoires.");
        }

        Integer bailId = bail.getId();

        List<RevisionLoyer> revisions =
                repo.findByBail_IdOrderByDateEffetAsc(bailId);

        if (revisions.isEmpty()) {
            Long loyerCourant = bail.getMontantLoyer();

            if (loyerCourant == null || loyerCourant <= 0) {
                throw new IllegalStateException(
                        "Aucun loyer valide n'est défini pour ce bail.");
            }

            return loyerCourant;
        }

        // La première révision conserve le loyer précédant cette révision.
        long montant = revisions.get(0).getAncienMontant();

        LocalDate premierJourMois = mois.withDayOfMonth(1);

        for (RevisionLoyer revision : revisions) {
            if (!revision.getDateEffet().isAfter(premierJourMois)) {
                montant = revision.getNouveauMontant();
            } else {
                break;
            }
        }

        if (montant <= 0) {
            throw new IllegalStateException(
                    "Le loyer applicable à ce mois est invalide.");
        }

        return montant;
    }*/
    
    

    private void verifierAgence(Agence agenceBail, Integer agenceCouranteId) {
        if (agenceBail == null
                || agenceBail.getId() == null
                || !Objects.equals(agenceBail.getId(), agenceCouranteId)) {
            throw new SecurityException(
                    "Ce bail n'appartient pas à votre agence.");
        }
    }
    
 
    
    @Transactional(readOnly = true)
    public Long montantApplicable(Bail bail, LocalDate dateMois) {

        if (bail == null || dateMois == null) {
            throw new IllegalArgumentException(
                    "Le bail et la date du mois sont obligatoires."
            );
        }

        LocalDate debutMois = dateMois.withDayOfMonth(1);

        List<RevisionLoyer> revisions =
                revisionLoyerRepository
                        .findByBail_IdOrderByDateEffetAsc(bail.getId());

        if (revisions == null || revisions.isEmpty()) {
            return bail.getMontantLoyer();
        }

        // Loyer de référence avant la première révision.
        Long montant = revisions.get(0).getAncienMontant();

        for (RevisionLoyer revision : revisions) {

            if (revision.getDateEffet() == null
                    || revision.getDateEffet().isAfter(debutMois)) {
                break;
            }

            montant = revision.getNouveauMontant();
        }

        return montant;
    }
    
   
    public List<RevisionLoyerDto> findRevisionsByBail(Integer bailId) {
        Bail bail = bailRepository.findById(bailId)
                .orElseThrow(() -> new IllegalArgumentException("Bail introuvable."));

        checkAgence(bail.getAgence(), getCurrentAgence().getId(),
                "Ce bail n'appartient pas à votre agence.");

        return revisionLoyerRepository.findByBailIdOrderByDateEffetAsc(bailId)
                .stream()
                .map(r -> new RevisionLoyerDto(r.getDateEffet(), r.getAncienMontant(), r.getNouveauMontant()))
                .toList();
    }
    
    
    private void checkAgence(Agence agenceEntity, Integer currentAgenceId, String message) {

	    if (agenceEntity == null) {
	        throw new SecurityException(message);
	    }

	    if (!java.util.Objects.equals(agenceEntity.getId(), currentAgenceId)) {
	        throw new SecurityException(message);
	    }
	}
    
}

package com.app.service.recouvre;

import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import com.app.dto.ContratBailView;
import com.app.entities.administration.Agence;
import com.app.entities.recouvre.Appartement;
import com.app.entities.recouvre.Bail;
import com.app.entities.recouvre.Locataire;
import com.app.entities.recouvre.RevisionLoyer;
import com.app.repositories.recouvre.RevisionLoyerRepository;
import com.app.util.MontantEnLettres;
//import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ContratBailService {

    private static final DateTimeFormatter DATE_FR = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final String VIDE = "........................";

    private final BailService bailService;
    private final RevisionLoyerRepository revisionLoyerRepository;
    private final SpringTemplateEngine templateEngine;

    // Règles du contrat, surchargeables dans application.properties
    @Value("${app.contrat.jour-limite-paiement:10}")
    private int jourLimitePaiement;
    @Value("${app.contrat.penalite-taux:10}")
    private int penaliteTaux;
    @Value("${app.contrat.preavis-mois:3}")
    private int preavisMois;
    @Value("${app.contrat.delai-resolutoire-jours:15}")
    private int delaiResolutoireJours;
    @Value("${app.contrat.tribunal:Abidjan}")
    private String tribunal;

    /**
     * Transactionnel : appartement.immeuble.bailleur et agence sont chargés en LAZY.
     * Le bail est chargé via BailService, donc déjà filtré sur l'agence connectée.
     */
    @Transactional(readOnly = true)
    public byte[] genererPdf(Integer bailId) {
        Bail bail = bailService.findByIdWithLocataireAndAppartement(bailId)
                .orElseThrow(() -> new IllegalArgumentException("Bail introuvable : " + bailId));

        Context ctx = new Context(Locale.FRANCE);
        ctx.setVariable("c", construireVue(bail));
        String html = templateEngine.process("locataire/bail/contrat-bail", ctx);

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(html, null);
            builder.toStream(out);
            builder.run();
            return out.toByteArray();
        } catch (Exception e) {
            throw new IllegalStateException("Échec de génération du contrat PDF", e);
        }
    }

    private ContratBailView construireVue(Bail b) {
        Locataire loc = b.getLocataire();
        Appartement app = b.getAppartement();
        Agence ag = b.getAgence();

        // Loyer initial = ancien montant de la 1re révision, sinon loyer courant du bail
        List<RevisionLoyer> revs = revisionLoyerRepository.findByBailIdOrderByDateEffetAsc(b.getId());
        long loyerActuel = nz(b.getMontantLoyer());
        long loyerInitial = revs.isEmpty() ? loyerActuel : nz(revs.get(0).getAncienMontant());

        List<ContratBailView.RevisionLigne> lignes = new ArrayList<>();
        for (RevisionLoyer r : revs) {
            lignes.add(new ContratBailView.RevisionLigne(
                    r.getDateEffet().format(DATE_FR),
                    fcfa(nz(r.getAncienMontant())),
                    fcfa(nz(r.getNouveauMontant()))));
        }

        long avance = nz(b.getAvance());
        long caution = nz(b.getCaution());
        long total = nz(b.getTotal());

        // Bailleur = propriétaire de l'immeuble (peut être absent : LEFT JOIN dans vos requêtes)
        String bailleur = VIDE;
        String immeubleNom = VIDE;
        String immeubleAdresse = VIDE;
        if (app.getImmeuble() != null) {
            immeubleNom = ou(app.getImmeuble().getNomImmeuble());
            immeubleAdresse = ou(app.getImmeuble().getAdresse());
            if (app.getImmeuble().getBailleur() != null) {
                bailleur = nomComplet(app.getImmeuble().getBailleur().getNom(),
                        app.getImmeuble().getBailleur().getPrenom());
            }
        }

        return ContratBailView.builder()
                .numero("BAIL-" + b.getDateDebut().getYear() + "-" + String.format("%06d", b.getId()))
                .lieuSignature(ag.getVille() != null && !ag.getVille().isBlank() ? ag.getVille() : "Abidjan")
                .dateSignature(LocalDate.now().format(DATE_FR))

                .agenceNom(ou(ag.getNom()))
                .agenceAdresse(ou(ag.getAdresse()))
                .agenceTelephone(ou(ag.getTelephone()))
                .agenceEmail(ou(ag.getEmail()))
                .bailleurNom(bailleur)

                .locataireNom(nomComplet(loc.getNom(), loc.getPrenom()))
                .locataireTelephone(ou(loc.getTelephone()))
                .locataireEmail(ou(loc.getEmail()))
                .locatairePiece(ou(loc.getTypepiece()))

                .bienLibelle(app.getNumAppart() + " - " + app.getLibelle())
                .bienComposition(composition(app))
                .immeubleNom(immeubleNom)
                .immeubleAdresse(immeubleAdresse)
                .usage(b.getUtilite() == null ? "habitation"
                        : b.getUtilite().name().toLowerCase(Locale.FRANCE).replace('_', ' '))

                .dateDebut(b.getDateDebut().format(DATE_FR))
                .dateFin(b.getDateFin().format(DATE_FR))
                .dureeMois(b.getDureeEnMois())

                .loyer(fcfa(loyerInitial))
                .loyerEnLettres(MontantEnLettres.convertir(loyerInitial))
                .revise(!revs.isEmpty())
                .loyerActuel(fcfa(loyerActuel))
                .loyerActuelEnLettres(MontantEnLettres.convertir(loyerActuel))
                .revisions(lignes)
                .charges(fcfa(nz(b.getMontantCharges())))
                .avance(fcfa(avance))
                .caution(fcfa(caution))
                .honoraire(fcfa(nz(b.getHonoraire())))
                .total(fcfa(total))
                .totalEnLettres(MontantEnLettres.convertir(total))
                .avanceNombreMois(mois(avance, loyerInitial))
                .cautionNombreMois(mois(caution, loyerInitial))

                .jourLimitePaiement(jourLimitePaiement)
                .penaliteTaux(penaliteTaux)
                .preavisMois(preavisMois)
                .delaiResolutoireJours(delaiResolutoireJours)
                .tribunal(tribunal)
                .build();
    }

    private static String composition(Appartement a) {
        List<String> p = new ArrayList<>();
        if (a.getNombreChambres() != null && a.getNombreChambres() > 0) {
            p.add(a.getNombreChambres() + " chambre(s)");
        }
        if (a.getNombreSalons() != null && a.getNombreSalons() > 0) {
            p.add(a.getNombreSalons() + " salon(s)");
        }
        if (a.getNombreSallesBain() != null && a.getNombreSallesBain() > 0) {
            p.add(a.getNombreSallesBain() + " salle(s) de bain");
        }
        if (a.getNombreBalcons() != null && a.getNombreBalcons() > 0) {
            p.add(a.getNombreBalcons() + " balcon(s)");
        }
        if (a.getEtage() != null && a.getEtage() > 0) {
            p.add("étage " + a.getEtage());
        }
        return p.isEmpty() ? "" : String.join(", ", p);
    }

    private static String nomComplet(String nom, String prenom) {
        String s = ((nom == null ? "" : nom) + " " + (prenom == null ? "" : prenom)).trim();
        return s.isEmpty() ? VIDE : s;
    }

    private static String ou(String s) {
        return s == null || s.isBlank() ? VIDE : s;
    }

    private static long nz(Long v) {
        return v == null ? 0L : v;
    }

    private static int mois(long montant, long loyer) {
        return loyer > 0 ? (int) Math.round((double) montant / loyer) : 0;
    }

    private static String fcfa(long v) {
        DecimalFormatSymbols s = new DecimalFormatSymbols(Locale.FRANCE);
        s.setGroupingSeparator(' ');
        return new DecimalFormat("#,##0", s).format(v);
    }
}
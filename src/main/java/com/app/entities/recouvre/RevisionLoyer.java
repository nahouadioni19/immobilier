package com.app.entities.recouvre;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.envers.Audited;
import org.springframework.format.annotation.DateTimeFormat;

import com.app.entities.BaseEntity;
import com.app.entities.administration.Agence;
import com.app.entities.administration.Utilisateur;
import com.app.utils.Constants;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Audited
@Entity
@Getter
@Setter
@NoArgsConstructor
@DynamicUpdate
@Table(
    name = "t_revision_loyer",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_revision_bail_date",
            columnNames = {"bail_id", "date_effet"}
        )
    }
)
public class RevisionLoyer extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bail_id", nullable = false)
    private Bail bail;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "agence_id", nullable = false)
    private Agence agence;

    @Column(name = "ancien_montant", nullable = false)
    private Long ancienMontant;

    @Column(name = "nouveau_montant", nullable = false)
    private Long nouveauMontant;

    @NotNull(message = "La date d'effet est obligatoire")
    @DateTimeFormat(pattern = Constants.FORMAT_DATE_DEFAULT)
    @Column(name = "date_effet", nullable = false)
    private LocalDate dateEffet;

    @Column(name = "date_enregistrement", nullable = false,
            updatable = false)
    private LocalDateTime dateEnregistrement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilisateur_id")
    private Utilisateur utilisateur;

    @Column(name = "motif", length = 500)
    private String motif;

    @PrePersist
    protected void avantEnregistrement() {
        if (dateEnregistrement == null) {
            dateEnregistrement = LocalDateTime.now();
        }
    }
}

package com.app.entities.recouvre;

import java.time.LocalDateTime;

import org.hibernate.envers.Audited;

import com.app.entities.BaseEntity;
import com.app.entities.administration.Agence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Audited
@Getter
@Setter
@NoArgsConstructor
@Table(
    name = "t_affectation_encaissement",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_affectation_encaisse_loyann",
            columnNames = {"encaisse_id", "loyann_id"}
        )
    }
)
public class AffectationEncaissement extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "encaisse_id", nullable = false)
    private Encaisse encaisse;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "loyann_id", nullable = false)
    private Loyann loyann;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "agence_id", nullable = false)
    private Agence agence;

    @Column(name = "montant_affecte", nullable = false)
    private Long montantAffecte;

    @Column(name = "date_affectation", nullable = false, updatable = false)
    private LocalDateTime dateAffectation;

    @PrePersist
    protected void avantEnregistrement() {
        if (dateAffectation == null) {
            dateAffectation = LocalDateTime.now();
        }
    }
}

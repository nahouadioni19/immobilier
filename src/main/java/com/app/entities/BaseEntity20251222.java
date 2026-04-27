package com.app.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.Instant;


@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public abstract class BaseEntity20251222 implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "per_entity_seq_gen"
    )
    @GenericGenerator(
        name = "per_entity_seq_gen",
        strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
        parameters = {
            // Hibernate créera une séquence PAR TABLE
            @Parameter(name = "prefer_sequence_per_entity", value = "true"),
            @Parameter(name = "increment_size", value = "1")
        }
    )
    @Column(name = "idt", updatable = false, nullable = false)
    protected Integer id;

    @CreatedBy
    @Column(name = "modifie_par", nullable = false)
    @JsonIgnore
    protected Integer modifiePar = 0;

    @CreatedDate
    @Column(name = "date_creation", nullable = false, updatable = false)
    @JsonIgnore
    protected Instant dateCreation;

    @LastModifiedDate
    @Column(name = "date_modification", nullable = false)
    @JsonIgnore
    protected Instant dateModification;

    @Version
    @Column(nullable = false)
    protected Integer version;

    protected BaseEntity20251222() {
        // requis par JPA
    }

    protected BaseEntity20251222(Integer id) {
        this.id = id;
    }

    protected BaseEntity20251222(Integer id, Integer modifiePar) {
        this.id = id;
        this.modifiePar = modifiePar != null ? modifiePar : 0;
    }

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        this.dateCreation = now;
        this.dateModification = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.dateModification = Instant.now();
    }

    /* ====== Champs techniques non persistés ====== */

    @Transient
    @JsonIgnore
    protected String json;

    @Transient protected boolean canView = true;
    @Transient protected boolean canEdit = true;
    @Transient protected boolean canDelete = true;
    @Transient protected boolean canCancel = false;
    @Transient protected boolean canPrint = true;
    @Transient protected boolean canValide = true;
    @Transient protected boolean hasBordereau = false;
    @Transient protected String prefixCode = "E";
    @Transient protected boolean exist = false;
    @Transient protected boolean replace = false;
}

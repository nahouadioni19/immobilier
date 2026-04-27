package com.app.entities.rapport;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.envers.Audited;

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.app.entities.BaseEntityWithLibelle;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Audited
@Entity
@Table(name = "t_ref_etat")
@DynamicUpdate
@Getter
@Setter
@NoArgsConstructor
//@SequenceGenerator(name = "ref_gen", sequenceName = "ref_etat_seq", allocationSize = 1)
public class RefEtat extends BaseEntityWithLibelle {

    private static final long serialVersionUID = 1L;

	/*
	 * @Override
	 * 
	 * @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ref_gen")
	 * 
	 * @Id public Integer getId() { return super.getId(); }
	 */
    
    private int ordre;

    private String nomFichier;

    @ManyToOne
    @JoinColumn(name = "groupe_id", referencedColumnName = "idt", nullable = false)
    private GroupeEtat groupe;

    @OneToMany(mappedBy = "refEtat", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<EtatAcces> etatAccess;

    @OneToMany(mappedBy = "refEtat", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<EtatParametre> etatParametres;

    @PrePersist
    public void prePersist() {
        if (this.etatAccess != null) {
            this.etatAccess.stream().parallel().forEach(access -> {
                access.setRefEtat(this);
                access.setModifiePar(getModifiePar());
            });
        }
        if (this.etatParametres != null) {
            this.etatParametres.stream().parallel().forEach(etatParam -> {
                etatParam.setRefEtat(this);
                etatParam.setModifiePar(getModifiePar());
            });
        }
    }
}

package com.app.entities.administration;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.envers.Audited;

import com.app.entities.BaseEntityWithCodeLibelle;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Audited
@Entity
@Table(name = "t_type_role")
@DynamicUpdate
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
//@SequenceGenerator(name = "type_role_gen", sequenceName = "type_role_seq", allocationSize = 1)
public class Typerole extends BaseEntityWithCodeLibelle {
	
    @OneToMany(mappedBy = "typeRole", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DroitAcces> droits;

    @PrePersist
    @PreUpdate
    public void prePersist() {
        if (droits != null)
            droits.stream().parallel().forEach(droit -> {
                droit.setTypeRole(this);
                droit.setModifiePar(getModifiePar());
            });
    }
}


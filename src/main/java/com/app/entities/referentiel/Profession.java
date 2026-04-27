package com.app.entities.referentiel;

import com.app.entities.BaseEntityWithLibelle;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.envers.Audited;

@Audited
@Entity
@Table(name = "t_profession")
@Getter
@Setter
@DynamicUpdate
//@SequenceGenerator(name = "profession_gen", sequenceName = "profession_seq", allocationSize = 1,initialValue = 1)
public class Profession extends BaseEntityWithLibelle {

    private static final long serialVersionUID = 1L;
	

}

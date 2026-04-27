package com.app.entities.abonne;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.envers.Audited;

import com.app.entities.BaseEntityWithCodeLibelle;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Audited
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "t_operateur")
@DynamicUpdate
public class Operateur extends BaseEntityWithCodeLibelle{

}

package com.app.entities.abonne;

import java.math.BigDecimal;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.envers.Audited;

import com.app.entities.BaseEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Audited
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "t_transaction")
@DynamicUpdate
public class Transaction extends BaseEntity{

	private String transactionId;

    @ManyToOne
    private Operateur operateur;

    private Long montant = 0L;
	
}

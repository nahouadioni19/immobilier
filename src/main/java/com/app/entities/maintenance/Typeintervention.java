package com.app.entities.maintenance;

import org.hibernate.annotations.DynamicUpdate;

import com.app.entities.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="type_intervention")
@Getter
@Setter
@NoArgsConstructor
@DynamicUpdate
public class Typeintervention extends BaseEntity{

    @Column(nullable=false, unique=true)
    private String libelle;

}
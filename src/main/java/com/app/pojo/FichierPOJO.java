package com.app.pojo;

import com.app.entities.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class FichierPOJO extends BaseEntity {

    private String nomOrigine;
    private String nom;
    private String lien;
    private String dossier;

    //

}


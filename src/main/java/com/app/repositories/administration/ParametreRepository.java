package com.app.repositories.administration;

import java.util.List;

import com.app.entities.administration.Parametre;
import com.app.repositories.base.BaseRepository;

public interface ParametreRepository extends BaseRepository<Parametre, Integer> {
    public List<Parametre> findByTypeCode(String code);
}

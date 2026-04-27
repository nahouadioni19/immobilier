package com.app.repositories.base;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import com.app.entities.BaseEntity;

@NoRepositoryBean
public interface BaseRepository<E extends BaseEntity, K> extends JpaRepository<E, K> {

    public Optional<E> findByCode(String code);

}


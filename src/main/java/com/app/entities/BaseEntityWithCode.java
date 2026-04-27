package com.app.entities;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;

import org.hibernate.envers.Audited;

import lombok.Getter;
import lombok.Setter;

@Audited
@Getter
@Setter
@MappedSuperclass
public abstract class BaseEntityWithCode extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Column(unique = true, length = 100, nullable = false)
    private String code;

    protected BaseEntityWithCode() {
        super();
    }
    
    protected BaseEntityWithCode(String code) {
        this();
        this.code = code;
    }

}
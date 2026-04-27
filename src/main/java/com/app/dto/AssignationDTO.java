package com.app.dto;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import com.app.utils.Constants;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssignationDTO {
    private Integer id;
    private Integer roleId;
    private String roleLibelle;
    @DateTimeFormat(pattern = Constants.FORMAT_DATE_DEFAULT)
    private LocalDate dateDebut;
    @DateTimeFormat(pattern = Constants.FORMAT_DATE_DEFAULT)
    private LocalDate dateFin;
    
}

package com.app.dto.revision;

import java.time.LocalDate;

public record RevisionLoyerDto(
		LocalDate dateEffet, 
		Long ancienMontant, 
		Long nouveauMontant
) {}
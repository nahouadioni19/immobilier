/*
 * package com.app.converters;
 * 
 * import org.springframework.core.convert.converter.Converter; import
 * org.springframework.stereotype.Component;
 * 
 * import com.app.entities.requerant.Pays; import
 * com.app.repositories.PaysRepository;
 * 
 * @Component public class PaysConverter implements Converter<String, Pays> {
 * 
 * private final PaysRepository paysRepository;
 * 
 * public PaysConverter(PaysRepository paysRepository) { this.paysRepository =
 * paysRepository; }
 * 
 * @Override public Pays convert(String payIndicatif) { if (payIndicatif == null
 * || payIndicatif.isEmpty()) { return null; } try { Long id =
 * Long.valueOf(payIndicatif); return paysRepository.findById(id).orElse(null);
 * } catch (NumberFormatException e) { return null; } } }
 */
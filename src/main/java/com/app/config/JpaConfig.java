package com.app.config;

import java.util.Locale;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import jakarta.annotation.PostConstruct;

@Configuration
public class JpaConfig {
	
	// Configuration pour la langue et le territoire
    @PostConstruct
    public void setDefaultLocale() {
        Locale.setDefault(Locale.US);
        System.setProperty("user.language", "en");
        System.setProperty("user.country", "US");
    }
    
  //VM arguments
    //  -Duser.language=en -Duser.country=US
 // Configuration Hibernate / JPA
    
	/*
	 * @Bean public JpaVendorAdapter jpaVendorAdapter() { HibernateJpaVendorAdapter
	 * adapter = new HibernateJpaVendorAdapter();
	 * adapter.setDatabasePlatform("org.hibernate.dialect.OracleDialect"); return
	 * adapter; }
	 */
}

package com.app.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

import com.app.controller.common.AbonnementInterceptor;
import com.app.controller.common.AgenceInterceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.storage.directory}")
    private String storageDirectory;

    private final AgenceInterceptor agenceInterceptor;
    private final AbonnementInterceptor abonnementInterceptor;
    
    public WebConfig(AgenceInterceptor agenceInterceptor, AbonnementInterceptor abonnementInterceptor) {
        this.agenceInterceptor = agenceInterceptor;
        this.abonnementInterceptor = abonnementInterceptor;
    }

    private static final String[] CLASSPATH_RESOURCE_LOCATIONS = {
            "classpath:/META-INF/resources/",
            "classpath:/resources/",
            "classpath:/static/",
            "classpath:/public/"
    };

    // =========================
    // 📁 Ressources statiques
    // =========================
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        registry.addResourceHandler("/**")
                .addResourceLocations(CLASSPATH_RESOURCE_LOCATIONS);

        registry.addResourceHandler("/dossiers/**")
                .addResourceLocations("file:///" + storageDirectory + "/");
    }

    // =========================
    // 🔐 Interceptor SaaS
    // =========================
    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(agenceInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/login",
                        "/logout",
                        "/error",
                        "/css/**",
                        "/js/**",
                        "/images/**",
                        "/dossiers/**" // ⚠️ important pour accès fichiers
                );
        
        registry.addInterceptor(abonnementInterceptor)
        .addPathPatterns("/**")
        .excludePathPatterns(
            "/login",
            "/abonnement/**",
            "/css/**",
            "/js/**",
            "/images/**"
        );
    }
    
}


/*@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.storage.directory}")
    private String storageDirectory;

    private static final String[] CLASSPATH_RESOURCE_LOCATIONS = {
            "classpath:/META-INF/resources/",
            "classpath:/resources/",
            "classpath:/static/",
            "classpath:/public/"
    };

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Ressources statiques classiques
        registry.addResourceHandler("/**")
                .addResourceLocations(CLASSPATH_RESOURCE_LOCATIONS);

        // Dossier d’upload (exposé sous /uploads/**)
        registry.addResourceHandler("/dossiers/**")
                .addResourceLocations("file:///" + storageDirectory + "/");
    }
}*/

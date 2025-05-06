// src/main/java/co/edu/uniquindio/logicapanaderia/config/WebConfig.java
package co.edu.uniquindio.logicapanaderia.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Sirve archivos físicos en /uploads/ desde tu carpeta local
        registry
                .addResourceHandler("/uploads/**")
                .addResourceLocations("file:src/main/resources/static/uploads/");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // Permite que desde Angular (localhost:4200) se puedan solicitar imágenes
        registry.addMapping("/uploads/**")
                .allowedOrigins("http://localhost:4200");
    }
}

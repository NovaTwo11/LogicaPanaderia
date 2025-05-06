// src/main/java/co/edu/uniquindio/logicapanaderia/config/JacksonConfig.java
package co.edu.uniquindio.logicapanaderia.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.hibernate6.Hibernate6Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper om = new ObjectMapper();
        // fechas Java8
        om.registerModule(new JavaTimeModule());
        om.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // para ignorar proxies Hibernate y no caer en LazyInitialization
        Hibernate6Module hmod = new Hibernate6Module();
        // opcional: hmod.disable(Hibernate6Module.Feature.FORCE_LAZY_LOADING);
        om.registerModule(hmod);

        return om;
    }
}

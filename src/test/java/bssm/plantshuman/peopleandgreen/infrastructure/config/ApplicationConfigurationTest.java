package bssm.plantshuman.peopleandgreen.infrastructure.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.io.FileSystemResource;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ApplicationConfigurationTest {

    @Test
    void usesEnvironmentBackedSecuritySensitiveConfiguration() {
        YamlPropertiesFactoryBean factoryBean = new YamlPropertiesFactoryBean();
        factoryBean.setResources(new FileSystemResource("src/main/resources/application.yml"));

        Properties properties = factoryBean.getObject();

        assertEquals("update", properties.getProperty("spring.jpa.hibernate.ddl-auto"));
        assertEquals("optional:file:.env[.properties]", properties.getProperty("spring.config.import"));
        assertTrue(properties.getProperty("spring.datasource.url").contains("${SPRING_DATASOURCE_URL"));
        assertEquals("${SPRING_DATASOURCE_USERNAME:}", properties.getProperty("spring.datasource.username"));
        assertEquals("${SPRING_DATASOURCE_PASSWORD:}", properties.getProperty("spring.datasource.password"));
        assertEquals("${AUTH_JWT_SECRET:}", properties.getProperty("auth.jwt.secret"));
        assertEquals("${SPRINGDOC_ENABLED:false}", properties.getProperty("springdoc.api-docs.enabled"));
        assertEquals("${SPRINGDOC_ENABLED:false}", properties.getProperty("springdoc.swagger-ui.enabled"));
        assertFalse(properties.getProperty("spring.datasource.url").contains("jojaemin.com"));
    }
}

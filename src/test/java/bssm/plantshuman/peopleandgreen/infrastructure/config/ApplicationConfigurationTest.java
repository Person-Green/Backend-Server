package bssm.plantshuman.peopleandgreen.infrastructure.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.io.FileSystemResource;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ApplicationConfigurationTest {

    @Test
    void usesConfiguredHibernateDdlAutoForMainApplicationConfig() {
        YamlPropertiesFactoryBean factoryBean = new YamlPropertiesFactoryBean();
        factoryBean.setResources(new FileSystemResource("src/main/resources/application.yml"));

        Properties properties = factoryBean.getObject();

        assertEquals("update", properties.getProperty("spring.jpa.hibernate.ddl-auto"));
    }
}

package bssm.plantshuman.peopleandgreen.infrastructure.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.io.FileSystemResource;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ApplicationConfigurationTest {

    @Test
    void ddlAutoIsEnvironmentVariableBased() {
        YamlPropertiesFactoryBean factoryBean = new YamlPropertiesFactoryBean();
        factoryBean.setResources(new FileSystemResource("src/main/resources/application.yml"));

        Properties properties = factoryBean.getObject();
        String ddlAuto = properties.getProperty("spring.jpa.hibernate.ddl-auto");

        assertNotNull(ddlAuto);
        // 환경변수 기반 설정이며, 기본값은 none (프로덕션 안전)
        assertTrue(ddlAuto.contains("HIBERNATE_DDL_AUTO") || ddlAuto.equals("none"),
                "ddl-auto must be env-var driven, got: " + ddlAuto);
    }
}

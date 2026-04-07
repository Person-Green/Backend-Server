package bssm.plantshuman.peopleandgreen;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class PeopleAndGreenApplication {

    public static void main(String[] args) {
        SpringApplication.run(PeopleAndGreenApplication.class, args);
    }

}

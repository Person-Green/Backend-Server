package bssm.plantshuman.peopleandgreen.recommendation.adapter.out.catalog;

import bssm.plantshuman.peopleandgreen.recommendation.application.port.out.LoadPlantCatalogPort;
import bssm.plantshuman.peopleandgreen.recommendation.domain.exception.FailedLoadDataException;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.PlantCatalogItem;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Component
@Profile("json-catalog")
@RequiredArgsConstructor
public class JsonPlantCatalogAdapter implements LoadPlantCatalogPort {

    private final ObjectMapper objectMapper;
    private List<PlantCatalogItem> catalog;

    @PostConstruct
    void load() {
        ClassPathResource resource = new ClassPathResource("recommendation/plant-catalog.json");
        try (InputStream inputStream = resource.getInputStream()) {
            this.catalog = List.copyOf(objectMapper.readValue(inputStream, new TypeReference<List<PlantCatalogItem>>() {
            }));
        } catch (IOException exception) {
            throw new FailedLoadDataException(exception);
        }
    }

    @Override
    public List<PlantCatalogItem> loadCatalog() {
        return catalog;
    }
}

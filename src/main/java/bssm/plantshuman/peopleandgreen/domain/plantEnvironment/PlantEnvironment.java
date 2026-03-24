package bssm.plantshuman.peopleandgreen.domain.plantEnvironment;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PlantEnvironment {

    @Id
    private String typeId;

    private String typeName;

    private String keyCondition;

    @Enumerated(EnumType.STRING)
    private List<Sunlight> sunlight;

    @Enumerated(EnumType.STRING)
    private List<Ventilation> ventilation;

    @Enumerated(EnumType.STRING)
    private List<Temperature> temperature;

    @Enumerated(EnumType.STRING)
    private List<Humidity> humidity;

    private String description;

}

package bssm.plantshuman.peopleandgreen.domain.plantEnvironment;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PlantEnvironment {

    @Id
    private String typeId;

    @Column(nullable = false)
    private String typeName;

    @Column(nullable = false)
    private String keyCondition;

    @Column(nullable = false)
    private String sunlight;

    @Column(nullable = false)
    private String ventilation;

    @Column(nullable = false)
    private String temperature;

    @Column(nullable = false)
    private String humidity;

    @Column(nullable = false)
    private String description;

}

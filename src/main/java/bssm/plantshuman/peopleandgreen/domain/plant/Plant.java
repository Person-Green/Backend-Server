package bssm.plantshuman.peopleandgreen.domain.plant;

import bssm.plantshuman.peopleandgreen.domain.plantEnvironment.PlantEnvironment;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Plant {

    @Id
    private String plantId;

    @Column(nullable = false)
    private String plantKoreanName;

    @Column(nullable = false)
    private String plantEnglishName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id", nullable = true)
    private PlantEnvironment plantEnvironment;

    @Enumerated(EnumType.STRING)
    private ManageDifficulty manageDifficulty;

    @Column(nullable = false)
    private String waterPeriod;

    @Column(nullable = false)
    private String appropriateTemperature;

    @Column(nullable = false)
    private String appropriateHumidity;

    @Column(nullable = false)
    private String sunlightRequirements;

    @Column(nullable = false)
    private String size;

    @Column(nullable = false)
    private String recommendedIndoorLocation;

    @Column(nullable = false)
    private String petSafety;

    @Column(nullable = false)
    private String description;

}

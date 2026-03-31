package bssm.plantshuman.peopleandgreen.domain.plant;

import bssm.plantshuman.peopleandgreen.domain.diagnosis.PlantEnvironment;
import bssm.plantshuman.peopleandgreen.domain.diagnosis.PlantEnvironmentMapping;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

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
    @JoinColumn(name = "primary_type_id", nullable = true)
    private PlantEnvironment primaryEnvironment;

    @OneToMany(mappedBy = "plant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlantEnvironmentMapping> environmentMappings = new ArrayList<>();

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
    @Enumerated(EnumType.STRING)
    private AirPurification airPurification;

    @Column(nullable = false)
    private String petSafety;

    @Column(nullable = false)
    private String description;

}

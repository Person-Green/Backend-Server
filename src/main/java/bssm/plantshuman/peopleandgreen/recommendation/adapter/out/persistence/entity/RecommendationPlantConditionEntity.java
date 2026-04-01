package bssm.plantshuman.peopleandgreen.recommendation.adapter.out.persistence.entity;

import bssm.plantshuman.peopleandgreen.recommendation.domain.model.PlacementType;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.SunlightLevel;
import bssm.plantshuman.peopleandgreen.recommendation.domain.model.VentilationLevel;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "recommendation_plant_condition")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecommendationPlantConditionEntity {

    @Id
    @Column(name = "plant_id", nullable = false, length = 30)
    private String plantId;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plant_id")
    private RecommendationPlantEntity plant;

    @Enumerated(EnumType.STRING)
    @Column(name = "sunlight_level", nullable = false)
    private SunlightLevel sunlightLevel;

    @Enumerated(EnumType.STRING)
    @Column(name = "ventilation_need", nullable = false)
    private VentilationLevel ventilationNeed;

    @Column(name = "temp_min", nullable = false)
    private int tempMin;

    @Column(name = "temp_max", nullable = false)
    private int tempMax;

    @Column(name = "humidity_min", nullable = false)
    private int humidityMin;

    @Column(name = "humidity_max", nullable = false)
    private int humidityMax;

    @Column(name = "water_cycle_min_days", nullable = false)
    private int waterCycleMinDays;

    @Column(name = "water_cycle_max_days", nullable = false)
    private int waterCycleMaxDays;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "recommendation_plant_placement", joinColumns = @JoinColumn(name = "plant_id"))
    @Column(name = "placement_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private Set<PlacementType> supportedPlacements = new LinkedHashSet<>();

    public RecommendationPlantConditionEntity(
            RecommendationPlantEntity plant,
            SunlightLevel sunlightLevel,
            VentilationLevel ventilationNeed,
            int tempMin,
            int tempMax,
            int humidityMin,
            int humidityMax,
            int waterCycleMinDays,
            int waterCycleMaxDays,
            Set<PlacementType> supportedPlacements
    ) {
        this.plant = plant;
        this.sunlightLevel = sunlightLevel;
        this.ventilationNeed = ventilationNeed;
        this.tempMin = tempMin;
        this.tempMax = tempMax;
        this.humidityMin = humidityMin;
        this.humidityMax = humidityMax;
        this.waterCycleMinDays = waterCycleMinDays;
        this.waterCycleMaxDays = waterCycleMaxDays;
        this.supportedPlacements.addAll(supportedPlacements);
    }
}

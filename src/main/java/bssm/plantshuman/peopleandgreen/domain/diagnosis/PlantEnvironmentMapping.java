package bssm.plantshuman.peopleandgreen.domain.diagnosis;

import bssm.plantshuman.peopleandgreen.domain.plant.Plant;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PlantEnvironmentMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plant_id", nullable = false)
    private Plant plant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id", nullable = false)
    private PlantEnvironment plantEnvironment;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FitType fit;

}